package com.vaibhav.user_service.exception;

public class RefreshTokenExpiredException extends RuntimeException{
    public RefreshTokenExpiredException(String message){
        super(message);
    }
}
