package com.supernova.lymming.github.auth;

import com.supernova.lymming.github.entity.User;
import com.supernova.lymming.github.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.ModelAndView;

@Service
@RequiredArgsConstructor
public class CustomOAuthUserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        GithubOAuth2UserInfo userInfo = new GithubOAuth2UserInfo(oAuth2User.getAttributes());

        //회원이 아닐 경우 회원 가입 진행
        User user = userRepository.findByGithubId(userInfo.getGithubId())
                .orElseGet(() -> createUser(userInfo));

        return (OAuth2User) CustomUserDetails.create(user, oAuth2User.getAttributes());
    }

    private User createUser(GithubOAuth2UserInfo userInfo) {
        // GitHub 정보로 사용자 객체 생성
        User user = User.builder()
                .githubId(userInfo.getGithubId())
                .nickname(null) // 초기값 설정 후, 사용자 입력 필요
                .stack(null) // 초기값 설정 후, 사용자 입력 필요
                .gender(null) // 초기값 설정 후, 사용자 입력 필요
                .job(null) // 초기값 설정 후, 사용자 입력 필요
                .bio(null) // 초기값 설정 후, 사용자 입력 필요
                .favorites(0) // 초기값 설정 후, 사용자 입력 필요
                .interests(null) // 초기값 설정 후, 사용자 입력 필요
                .devStyle(null) // 초기값 설정 후, 사용자 입력 필요
                .build();
        return userRepository.save(user); // 사용자 저장
    }

}
