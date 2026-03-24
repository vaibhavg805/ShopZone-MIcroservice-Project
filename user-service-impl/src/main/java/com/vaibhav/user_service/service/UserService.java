package com.vaibhav.user_service.service;

import com.vaibhav.user_service.client.dto.ProductResponseDto;
import com.vaibhav.user_service.dto.ChangePassword;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserService {
    public void changePassword(ChangePassword changePassword);
    public String requestPasswordReset(String email);
    public void resetPassword(String token, String newPassword);
    public String callProductService();
    public List<ProductResponseDto> getAllProducts();
}
