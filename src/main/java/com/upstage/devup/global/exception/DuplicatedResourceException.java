package com.upstage.devup.global.exception;

/**
 * 이미 존재하는 리소스를 중복으로 저장하려 할 때 발생하는 예외
 */
public class DuplicatedResourceException extends RuntimeException {
    public DuplicatedResourceException(String message) {
        super(message);
    }
}
