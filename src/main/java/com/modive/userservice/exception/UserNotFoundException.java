package com.modive.userservice.exception;

public class UserNotFoundException extends CustomException {
    public UserNotFoundException() {
        super(new UserNotFoundError());
    }
}