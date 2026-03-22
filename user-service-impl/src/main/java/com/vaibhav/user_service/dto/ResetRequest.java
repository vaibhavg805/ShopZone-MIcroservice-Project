package com.vaibhav.user_service.dto;

public record ResetRequest(String token, String newPassword) {
}
