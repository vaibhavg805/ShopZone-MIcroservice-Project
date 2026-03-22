package com.vaibhav.user_service.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressResponseDto {

    private Long id;
    private String addressLine1;
    private String city;
    private Boolean isDefault;

}
