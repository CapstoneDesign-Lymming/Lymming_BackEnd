package com.supernova.lymming.github.auth;

import com.supernova.lymming.github.entity.Gender;
import com.supernova.lymming.github.entity.LoginType;
import com.supernova.lymming.github.entity.User;
import com.supernova.lymming.github.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuthUserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        // OAuth2UserREquest를 받아 깃허브에서 사용자 정보를 로드하는 메소드

        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        // 기본 사용자 로딩 로직을 호출해 깃허브에서 사용자 정보를 가져온다.
        // 사용자 로딩이란, 사용자의 정보를 외부 인증 제공자로부터 가져오는 과정

        GithubOAuth2UserInfo userInfo = new GithubOAuth2UserInfo(oAuth2User.getAttributes());
        // 깃허브에서 받은 사용자 정보를 GitjubOAuth2UserInfo 객체에 저장

        User user = userRepository.findByServerNickname(userInfo.getServerNickName())
                // DB에서 깃허브 ID로 사용자를 조회한다.
                .orElseGet(() -> createUser(userInfo));

        return (OAuth2User) CustomUserDetails.create(user, oAuth2User.getAttributes());
    }

    public User createUser(GithubOAuth2UserInfo userInfo) {
        // GitHub 정보로 사용자 객체 생성
        User user = User.builder()
                .serverNickname(userInfo.getServerNickName())
                .nickname("") // 초기값 설정 후, 사용자 입력 필요
                .stack("") // 초기값 설정 후, 사용자 입력 필요
                .gender(Gender.기타) // 초기값 설정 후, 사용자 입력 필요
                .job("") // 초기값 설정 후, 사용자 입력 필요
                .bio("") // 초기값 설정 후, 사용자 입력 필요
                .favorites(0) // 초기값 설정 후, 사용자 입력 필요
                .position("") // 초기값 설정 후, 사용자 입력 필요
                .devStyle("") // 초기값 설정 후, 사용자 입력 필요
                .loginType(LoginType.Github)
                .build();
        return userRepository.save(user); // 사용자 저장
    }

}