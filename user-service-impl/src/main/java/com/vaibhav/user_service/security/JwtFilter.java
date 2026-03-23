package com.vaibhav.user_service.security;

import com.vaibhav.user_service.security.customservice.CustomUserDetailService;
import com.vaibhav.user_service.security.customservice.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailService customUserDetailService;
    public JwtFilter(JwtUtil jwtUtil, CustomUserDetailService customUserDetailService){
        this.jwtUtil=jwtUtil;
        this.customUserDetailService=customUserDetailService;
    }
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            logger.info("Inside Filter Chain...");
        String userId = request.getHeader("X-User-Id");
        String role = request.getHeader("X-User-Role");
        logger.info(userId);
        logger.info(role);
            String header = request.getHeader("Authorization");

            if(header != null && header.startsWith("Bearer ")){
                String jwt_token = header.substring(7);
                String username = jwtUtil.extractUsername(jwt_token);

                if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                         CustomUserDetails customUserDetails  = (CustomUserDetails) customUserDetailService.loadUserByUsername(username);

                    System.out.println("USERNAME FROM TOKEN = " + username);
                    System.out.println("VALIDATION = " + jwtUtil.isValidate(customUserDetails.getUsername(), jwt_token));
                    logger.info("Creating Authentication Object");
                    if(jwtUtil.isValidate(customUserDetails.getUsername(),jwt_token)){
                             UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                                     new UsernamePasswordAuthenticationToken(customUserDetails,null,customUserDetails.getAuthorities());

                             SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                        System.out.println("AUTH AFTER SET = " + SecurityContextHolder.getContext().getAuthentication());

                    }
                }
            }

        filterChain.doFilter(request,response);

    }
}
