package com.vaibhav.user_service.controller;

import com.vaibhav.user_service.dto.*;
import com.vaibhav.user_service.exception.RefreshTokenExpiredException;
import com.vaibhav.user_service.exception.RefreshTokenNotFoundException;
import com.vaibhav.user_service.exception.UserAlreadyExistException;
import com.vaibhav.user_service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("/api")
@RequestMapping("/user/authentication")
public class AuthController {
    private AuthService authenticationService;
    public AuthController(AuthService authenticationService){
        this.authenticationService=authenticationService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> userRegisterServiceApi(@Valid @RequestBody UserRequestDto userRequestDto){
        try {
            UserResponseDto response = authenticationService.saveDataForUser(userRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (UserAlreadyExistException e) {
            ErrorResponse errorResponse = new ErrorResponse("User already exists", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

    }

    @PostMapping("/auth/register/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminRegisterServiceApi(@Valid @RequestBody UserRequestDto userRequestDto){
        try {
            UserResponseDto response = authenticationService.saveDataForAdmin(userRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (UserAlreadyExistException e) {
            ErrorResponse errorResponse = new ErrorResponse("User already exists", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

    }

    @PostMapping("/auth/register/seller")
    public ResponseEntity<?> sellerRegisterServiceApi(@Valid @RequestBody UserRequestDto userRequestDto){
        try {
            UserResponseDto response = authenticationService.saveDataForSeller(userRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (UserAlreadyExistException e) {
            ErrorResponse errorResponse = new ErrorResponse("User already exists", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

    }
    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> loginUserServiceApi(@RequestBody LoginRequest loginRequest){
        LoginResponse response = authenticationService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<?> generateNewAccessToken(@RequestBody AccessToken accessToken){
        try {
            LoginResponse response = authenticationService.validateAndGenerateAccessToken(accessToken);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RefreshTokenExpiredException ex){
            ErrorResponse errorResponse = new ErrorResponse("Refresh Token Expired", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (RefreshTokenNotFoundException ex){
            ErrorResponse errorResponse = new ErrorResponse("Refresh Token Not Found", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        catch (Exception ex){
            ErrorResponse errorResponse = new ErrorResponse("An Unexpected Error Occurred", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logOutApi(){
        String res  = authenticationService.logOut();
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

}
