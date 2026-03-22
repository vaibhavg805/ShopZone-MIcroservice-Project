package com.vaibhav.user_service.externalconfig;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class BeanConfig {

    @Bean
    ModelMapper modelMapper(){
        return new ModelMapper();
    }
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

}
