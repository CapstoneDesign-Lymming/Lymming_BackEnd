
package com.supernova.lymming.jwt;

import com.supernova.lymming.github.auth.CustomUserDetails;
import com.supernova.lymming.github.repository.UserRepository;
import io.jsonwebtoken.*;

import java.util.*;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.stream.Collectors;

@Component
@Slf4j
@ComponentScan
public class JwtTokenProvider {

    private final Key SECRET_KEY;
    private final String COOKIE_REFRESH_TOKEN_KEY;
    private final long ACCESS_TOKEN_EXPIRE_LENGTH = 1000L * 60 * 60;
    private final long REFRESH_TOKEN_EXPIRE_LENGTH = 1000L * 60 * 60 * 24 * 7;
    private final String AUTHORITIES_KEY = "role";
    private final String EMAIL_KEY = "email";

    private UserRepository userRepository;

    public JwtTokenProvider(@Value("${custom.jwt.secretKey}")String secretKey, @Value("${app.auth.token.refresh-cookie-key}")String cookieKey, UserRepository userRepository) {
        this.SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.COOKIE_REFRESH_TOKEN_KEY = cookieKey;
        this.userRepository = userRepository;
    }

    private CustomUserDetails convertToCustomUserDetails(OAuth2User oAuth2User) {
        // 'id'와 'nickname' 값을 안전하게 추출하기 전에 null 체크
        Object idObj = oAuth2User.getAttributes().get("id");
        Object nicknameObj = oAuth2User.getAttributes().get("nickname");

        // 'id'와 'nickname'이 null이면 예외 처리
        if (idObj == null || nicknameObj == null) {
            throw new IllegalArgumentException("Kakao user attributes are missing: id or nickname is null.");
        }

        // id와 nickname을 안전하게 추출
        Long userId = Long.valueOf(idObj.toString());
        String nickname = nicknameObj.toString();

        // 필요한 정보로 CustomUserDetails 객체 생성
        // List<GrantedAuthority>로 권한을 설정
        return new CustomUserDetails(
                userId,
                nickname, // nickname 추가
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")) // 권한을 List로 감쌈
        );
    }

    public String createAccessToken(Authentication authentication) {
        log.info("엑세스 터큰 발급 들어옴");
        Date now = new Date();
        Date validity = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_LENGTH);

        String userId = null;
        String email = null;

        // authentication.getPrincipal()을 확인하고 DefaultOAuth2User 또는 CustomUserDetails로 변환
        Object principal = authentication.getPrincipal();

        CustomUserDetails user = null;

        // 로그인 타입이 카카오 OAuth2인 경우에만 처리
        if (principal instanceof OAuth2User) {
            log.info("카카오 로그인 타입 확인하는 조건문 들어옴");
            OAuth2User oAuth2User = (OAuth2User) principal;
            log.info("카카오 로그인 타입 확인하는 oAuth2User:{}", oAuth2User);

            // OAuth2 사용자가 카카오 로그인인지 확인 (예: provider 정보를 사용)
            log.info("카카오 로그인 타입 확인하기");
            if (principal instanceof OAuth2User) {
                log.info("Kakao 와 provider가 같다");
                // 카카오 로그인일 경우에만 CustomUserDetails로 변환
                user = convertToCustomUserDetails(oAuth2User);
                log.info("user:{}", user);
            } else {
                user = (CustomUserDetails) principal;
            }
            if (user == null) {
                throw new IllegalArgumentException("로그인 된 사용자를 찾을 수 없습니다");
            }
        }

        userId = user.getName();
        email = user.getUsername();
        log.info("userId:{}", userId);
        log.info("email:{}", email);

        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(userId)
                .claim(AUTHORITIES_KEY, role)
                .claim(EMAIL_KEY, email)
                .setIssuer("bok")
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public void createRefreshToken(Authentication authentication, HttpServletResponse response) {
        log.info("리프래시 들어옴");
        Date now = new Date();
        Date validity = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_LENGTH);

        String refreshToken = Jwts.builder()
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .setIssuer("bok")
                .setIssuedAt(now)
                .setExpiration(validity)
                .compact();

        saveRefreshToken(authentication, refreshToken);
        ResponseCookie cookie = ResponseCookie.from(COOKIE_REFRESH_TOKEN_KEY, refreshToken)
                .httpOnly(false)
                .secure(true)
                .sameSite("None")
                .maxAge(REFRESH_TOKEN_EXPIRE_LENGTH / 1000)
                .path("/")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
    private void saveRefreshToken(Authentication authentication, String refreshToken) {

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        long id = Long.parseLong(user.getName());
        userRepository.updateRefreshToken(id, refreshToken);
    }

    public Authentication getAuthentication(String accessToken) {
        log.info("getAuthentication에서의 accessToken1:{}", accessToken);
        Claims claims = parseClaims(accessToken);
        log.info("getAuthentication에서의 accessToken2:{}", accessToken);
        log.info("getAuthentication에서의 claims:{}", claims);

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        CustomUserDetails principal = new CustomUserDetails(Long.valueOf(claims.getSubject()), claims.get(EMAIL_KEY, String.class),authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public Boolean validateToken(String token) {
        try {
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

    // Access Token 만료시 갱신 때 사용할 정보를 얻기 위해 Claim 리턴
    // Access Token 만료 시 갱신 때 사용할 정보를 얻기 위해 Claim 리턴
    private Claims parseClaims(String accessToken) {
        log.info("메소드 들어옴");
        log.info("parseClaims의 accessToken: {}", accessToken);
        try {
            log.info("try문 진입");

            // JWT 파싱 및 클레임 추출
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();

            // 클레임 로깅
            log.info("Parsed Claims: {}", claims);

            return claims; // 클레임 반환
        } catch (ExpiredJwtException e) {
            log.warn("JWT가 만료되었습니다. 만료된 클레임 반환: {}", e.getClaims());
            return e.getClaims(); // 만료된 클레임 반환
        } catch (SignatureException e) {
            log.error("유효하지 않은 JWT 서명: {}", e.getMessage());
            throw e; // 예외 발생
        } catch (MalformedJwtException e) {
            log.error("잘못된 형식의 JWT: {}", e.getMessage());
            throw e; // 예외 발생
        } catch (Exception e) {
            log.error("JWT 파싱 중 오류 발생: {}", e.getMessage());
            throw e; // 예외 발생
        }
    }
}
