package com.vaibhav.user_service.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FeignClientInterceptor implements RequestInterceptor {

    @Autowired
    private HttpServletRequest request;
    /**
     * @param template
     */
    @Override
    public void apply(RequestTemplate template) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            template.header("Authorization", authHeader);
            log.info("Passing Jwt internally to Product-Service...");
        }
    }
}
