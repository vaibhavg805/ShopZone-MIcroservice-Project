package com.vaibhav.user_service.exception;

import java.io.Serial;

public class RefreshTokenNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public RefreshTokenNotFoundException(String message) {
        super(message);
    }
}
