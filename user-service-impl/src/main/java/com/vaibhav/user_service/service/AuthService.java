package com.vaibhav.user_service.service;

import com.vaibhav.user_service.dto.*;

public interface AuthService {

    public UserResponseDto saveDataForUser(UserRequestDto userRequestDto);
    public UserResponseDto saveDataForAdmin(UserRequestDto userRequestDto);
    public UserResponseDto saveDataForSeller(UserRequestDto userRequestDto);
    public LoginResponse login(LoginRequest loginRequest);
    public String generateRefreshToken(String username);
    public LoginResponse validateAndGenerateAccessToken(AccessToken accessToken);
    public String logOut();
}
