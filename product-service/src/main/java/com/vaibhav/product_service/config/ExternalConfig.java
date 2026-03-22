package com.vaibhav.product_service.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "file")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExternalConfig {
    private String uploadDir;
}
