package com.supernova.lymming.kakao.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponse {
    private Long id;
    private String nickname;
    private String token;

    public LoginResponse(Long id, String nickname,String token){
        this.id = id;
        this.nickname=nickname;
        this.token=token;
    }
}
