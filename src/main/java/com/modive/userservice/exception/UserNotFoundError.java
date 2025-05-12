package com.modive.userservice.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundError implements ErrorCode {

    private final HttpStatus httpStatus;
    private final String message;

    public UserNotFoundError() {
        this.httpStatus = HttpStatus.NOT_FOUND;
        this.message = "요청한 사용자를 찾을 수 없습니다.";
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
