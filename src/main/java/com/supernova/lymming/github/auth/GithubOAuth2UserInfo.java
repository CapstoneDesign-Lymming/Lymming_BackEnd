package com.supernova.lymming.github.auth;

import java.util.Map;

public class GithubOAuth2UserInfo {
    private Map<String, Object> attributes;

    public GithubOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getServerNickName() {
        return (String) attributes.get("login");  // GitHub의 로그인 ID를 가져옵니다.
    }
}