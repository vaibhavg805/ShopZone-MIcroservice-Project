package com.vaibhav.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {

    @NotBlank(message = "Username is required")
    private String username;

    private String firstName;

    private String lastName;

    @NotBlank(message = "Password is Required")
    private String password;

    @Email
    @NotBlank
    private String email;

}
