package com.vaibhav.user_service.security;

import org.springframework.stereotype.Component;

@Component
public class UserContext {

    private static final ThreadLocal<String> currentUserId = new ThreadLocal<>();
    private static final ThreadLocal<String> currentJwtToken = new ThreadLocal<>();

    public static void setCurrentUserId(String userId){
        currentUserId.set(userId);
    }

    public static String getCurrentUserId(){
        return currentUserId.get();
    }

    public static void setCurrentJwtToken(String jwtToken) {
        currentJwtToken.set(jwtToken);
    }

    public static String getCurrentJwtToken() {
        return currentJwtToken.get();
    }

    public static void clear() {
        currentUserId.remove();
        currentJwtToken.remove();
    }
}
