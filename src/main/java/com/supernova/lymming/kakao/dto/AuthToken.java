package com.supernova.lymming.kakao.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthToken {
    private String accessToken;
    private String refreshToken;
    private String grantType;
    private Long expiresIn;

    public static AuthToken of(String accessToken, String refreshToken, String grantType, Long expiresIn){
        return new AuthToken(accessToken, refreshToken, grantType, expiresIn);
    }
}
