package com.vaibhav.user_service.externalconfig;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@Slf4j
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
