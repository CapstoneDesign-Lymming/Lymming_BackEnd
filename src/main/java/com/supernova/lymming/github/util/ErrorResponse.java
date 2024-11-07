package com.supernova.lymming.github.util;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final int code;
    private final String message;

    public ErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
