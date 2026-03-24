package com.vaibhav.user_service.controller;

import com.vaibhav.user_service.client.dto.ProductResponseDto;
import com.vaibhav.user_service.dto.ChangePassword;
import com.vaibhav.user_service.dto.ErrorResponse;
import com.vaibhav.user_service.dto.ForgotRequest;
import com.vaibhav.user_service.dto.ResetRequest;
import com.vaibhav.user_service.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService){
        this.userService=userService;
    }
    @GetMapping("/test")
    public String testUser(){
        System.out.println("CONTROLLER HIT");
        return "API RUNNING FINE";
    }

    @PutMapping("/change_password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePassword changePassword){
        try {
            userService.changePassword(changePassword);
            return ResponseEntity.status(HttpStatus.OK).body("Password Reset Successfully");
        }
        catch (AccessDeniedException ex){
            ErrorResponse errorResponse = new ErrorResponse("Access Denied", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (BadCredentialsException ex){
            ErrorResponse errorResponse = new ErrorResponse("Bad Credentials Exception", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        catch (Exception ex){
            ErrorResponse errorResponse = new ErrorResponse("An Unexpected Error Occurred", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgot(@RequestBody ForgotRequest req) {
        try {
           String result = userService.requestPasswordReset(req.email());
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }
        catch(UsernameNotFoundException ex){
            ErrorResponse errorResponse = new ErrorResponse("User Email Not Found Exception", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch(Exception e){
            ErrorResponse errorResponse = new ErrorResponse("An Unexpected Error Occurred", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> reset(@RequestBody ResetRequest req) {
        try {
            userService.resetPassword(req.token(), req.newPassword());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.status(HttpStatus.OK).body("Password Reset Successfully");
    }

    @GetMapping("/call-product")
    public ResponseEntity<String> callProduct() {
        return ResponseEntity.ok(userService.callProductService());
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<List<ProductResponseDto>> getProducts() {
        List<ProductResponseDto> products = userService.getAllProducts();
        return ResponseEntity.ok(products);
    }

}
