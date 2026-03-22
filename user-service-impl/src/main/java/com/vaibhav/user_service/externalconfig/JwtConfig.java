package com.vaibhav.user_service.externalconfig;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JwtConfig {
    private String secret;
}
