package com.supernova.lymming.github.jwt;

import com.supernova.lymming.github.auth.CustomUserDetails;
import com.supernova.lymming.github.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {

    // JWT 암호화에 사용할 SECRET_KEY
    private final Key SECRET_KEY;

    // 리프레시 토큰용 쿠키 키
    private final String COOKIE_REFRESH_TOKEN_KEY;

    // 엑세스 토큰 만료 시간 (1시간)
    private final long ACCESS_TOKEN_EXPIRE_LENGTH = 1000L * 60 * 60;

    // 리프레시 토큰 만료 시간 (1주일)
    private final long REFRESH_TOKEN_EXPIRE_LENGTH = 1000L * 60 * 60 * 24 * 7;

    // JWT 클레임에 저장할 사용자 권한 키
    private final String AUTHORITIES_KEY = "role";

    // JWT 클레임에 저장할 깃허브 ID 키
    private final String GITHUB_ID_KEY = "githubId";

    // 사용자 정보를 관리하는 UserRepository
    private final UserRepository userRepository;

    // 생성자 - secretKey와 cookieKey를 외부 설정에서 받아와서 초기화
    public JwtTokenProvider(@Value("${custom.jwt.secretKey}") String secretKey,
                            @Value("${app.auth.token.refresh-cookie-key}") String cookieKey,
                            UserRepository userRepository) {
        // Base64 인코딩된 secretKey를 디코딩하여 SECRET_KEY 초기화
        this.SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.COOKIE_REFRESH_TOKEN_KEY = cookieKey;
        this.userRepository = userRepository;
    }

    /**
     * 사용자 인증 정보를 기반으로 JWT 액세스 토큰 생성
     * @param authentication 인증된 사용자 정보
     * @return 생성된 액세스 토큰 문자열
     */
    public String createAccessToken(Authentication authentication) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_LENGTH);

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        String githubId = user.getUsername(); // 사용자 깃허브 ID
        String userId = String.valueOf(user.getUser().getUserId()); // 사용자 ID를 가져오는 부분

        // 사용자의 권한 목록을 ","로 구분하여 문자열로 변환
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // 각 GrantedAuthority에서 권한 문자열을 가져옴
                .collect(Collectors.joining(",")); // 권한 문자열을 ","로 연결

        // JWT 빌더를 사용해 토큰 생성
        return Jwts.builder()
                .setSubject(userId)                        // 토큰 주체 (userId)
                .claim(AUTHORITIES_KEY, role)              // 클레임에 권한 추가
                .claim(GITHUB_ID_KEY, githubId)            // 클레임에 깃허브 ID 추가
                .setIssuer("bok")                          // 토큰 발급자
                .setIssuedAt(now)                          // 토큰 발급 시간
                .setExpiration(validity)                   // 토큰 만료 시간
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // 서명 알고리즘 설정
                .compact();                                // JWT 문자열 생성
    }

    /**
     * 리프레시 토큰 생성 및 응답에 쿠키로 추가
     * @param authentication 인증된 사용자 정보
     * @param response HTTP 응답 객체
     */
    public void createRefreshToken(Authentication authentication, HttpServletResponse response) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_LENGTH);

        // 리프레시 토큰 생성
        String refreshToken = Jwts.builder()
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // 서명 설정
                .setIssuer("bok")                              // 발급자 설정
                .setIssuedAt(now)                              // 발급 시간 설정
                .setExpiration(validity)                       // 만료 시간 설정
                .compact();

        // 리프레시 토큰을 데이터베이스에 저장
        saveRefreshToken(authentication, refreshToken);

        // 리프레시 토큰을 쿠키에 추가하여 응답 헤더에 포함
        ResponseCookie cookie = ResponseCookie.from(COOKIE_REFRESH_TOKEN_KEY, refreshToken)
                .httpOnly(false)
                .secure(true)
                .sameSite("None")
                .maxAge(REFRESH_TOKEN_EXPIRE_LENGTH / 1000)   // 쿠키 만료 시간 설정
                .path("/")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    /**
     * 리프레시 토큰을 데이터베이스에 저장
     * @param authentication 인증된 사용자 정보
     * @param refreshToken 생성된 리프레시 토큰
     */
    private void saveRefreshToken(Authentication authentication, String refreshToken) {
        // 사용자 세부 정보를 가져옵니다.
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        // User 객체에서 ID를 가져옵니다. ID는 Integer 타입으로 가정합니다.
        Long userId = user.getUser().getUserId(); // User 객체의 ID를 가져옵니다.

        // 리프레시 토큰을 업데이트하는 메서드 호출
        userRepository.updateRefreshToken(userId, refreshToken);
    }

    /**
     * 액세스 토큰에서 사용자 인증 객체를 추출
     * @param accessToken 액세스 토큰
     * @return 추출된 사용자 인증 객체
     */
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        // 권한 정보 문자열을 SimpleGrantedAuthority 목록으로 변환
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        // 깃허브 ID 및 권한 정보로 사용자 세부 정보 객체 생성
        CustomUserDetails principal = new CustomUserDetails(Long.valueOf(claims.getSubject()),
                claims.get(GITHUB_ID_KEY, String.class),
                authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * 토큰의 유효성 검증
     * @param token JWT 토큰
     * @return 토큰이 유효하면 true, 그렇지 않으면 false
     */
    public Boolean validateToken(String token) {
        try {
            // 토큰의 서명 검증
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
            return true;
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (JwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalStateException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    /**
     * JWT 토큰에서 클레임을 파싱하여 반환 (만료된 토큰도 클레임을 추출할 수 있음)
     * @param accessToken JWT 액세스 토큰
     * @return 파싱된 클레임
     */
    private Claims parseClaims(String accessToken) {
        try {
            // 유효한 토큰인 경우 클레임을 반환
            return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰의 경우에도 클레임 반환
            return e.getClaims();
        }
    }
}

