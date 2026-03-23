package com.vaibhav.user_service.security;

import com.vaibhav.user_service.security.customservice.CustomUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailService customUserDetailService;
    private final JwtFilter jwtFilter;
    private final AuthEntryPoint authEntryPoint;

    public SecurityConfig(CustomUserDetailService customUserDetailService,JwtFilter jwtFilter,AuthEntryPoint authEntryPoint){
        this.customUserDetailService=customUserDetailService;
        this.jwtFilter=jwtFilter;
        this.authEntryPoint=authEntryPoint;
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(){
      //  DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailService);
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(customUserDetailService);
        return new ProviderManager(provider);
    }

//    @Bean
//    public AuthenticationManager authenticationManager(
//            AuthenticationConfiguration configuration) throws Exception {
//        return configuration.getAuthenticationManager();
//    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
            return httpSecurity.csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/user/authentication/auth/register","/user/authentication/auth/login").permitAll()
                            .requestMatchers("/user/authentication/auth/register/seller").permitAll()
                            .requestMatchers("/user/authentication/auth/register/admin").hasRole("ADMIN")
                            .requestMatchers("/user/authentication/auth/refresh").permitAll()
                            .requestMatchers("/user/reset-password","/user/forgot-password").permitAll()
                            .requestMatchers("/user/**").authenticated()
                            .requestMatchers(("/user/address/users/address")).authenticated()
                            .anyRequest().authenticated()
                    )
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                    .exceptionHandling(ex -> ex.authenticationEntryPoint(authEntryPoint))
                    .headers(header -> header.xssProtection(HeadersConfigurer.XXssConfig::disable))
                    .build();
    }

}
