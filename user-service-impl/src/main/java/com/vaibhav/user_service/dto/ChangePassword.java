package com.vaibhav.user_service.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangePassword {
    private String newPassword;
    private String oldPassword;
}
