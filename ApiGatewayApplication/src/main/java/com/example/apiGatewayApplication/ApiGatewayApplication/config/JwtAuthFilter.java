package com.example.apiGatewayApplication.ApiGatewayApplication.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

@Component
@Slf4j
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final String secret_key;
    private final Key key;
    private final JwtParser parser;
    public CustomPropertyConfig customPropertyConfig;

    public JwtAuthFilter(CustomPropertyConfig customPropertyConfig){
        this.secret_key = customPropertyConfig.getSecret();
        this.key       = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret_key));
        this.parser    = Jwts.parserBuilder().setSigningKey(key).build();
    }

    private static final List<String> OPEN_ENDPOINTS = List.of(
            "/user/authentication/auth/login",
            "/user/authentication/auth/register"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        log.info("Gateway received request for path: {}", path);

        // Step 1: Is this an open endpoint
        if (isOpenEndpoint(path)) {
            log.info("Open endpoint — skipping JWT check for: {}", path);
            return chain.filter(exchange);   // just forward, no JWT check
        }

        // Step 2: Get Authorization header
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or malformed Authorization header");
            return sendUnauthorized(exchange, "Missing Authorization header");
        }

        // Step 3: Extract token
        String token = authHeader.substring(7);  // removes "Bearer "

        // Step 4: Validate token
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String userId = claims.getSubject();
            String role   = claims.get("role", String.class);

            log.info("JWT valid for userId: {}, role: {}", userId, role);

            // Step 5: Pass user info to downstream services
            // Now user-service and product-service can read these headers
            // They don't need to validate JWT themselves!
            ServerHttpRequest enrichedRequest = exchange.getRequest()
                    .mutate()
                    .header("X-User-Id",   userId)
                    .header("X-User-Role", role)
                    .build();

            return chain.filter(exchange.mutate().request(enrichedRequest).build());

        } catch (ExpiredJwtException e) {
            log.warn("JWT expired: {}", e.getMessage());
            return sendUnauthorized(exchange, "Token has expired");

        } catch (JwtException e) {
            log.warn("JWT invalid: {}", e.getMessage());
            return sendUnauthorized(exchange, "Invalid token");
        }
    }

    // check if path is open
    private boolean isOpenEndpoint(String path) {
        return OPEN_ENDPOINTS.stream().anyMatch(path::contains);
    }

    // send 401 response
    private Mono<Void> sendUnauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");

        String body = "{\"error\": \"" + message + "\"}";
        DataBuffer buffer = response.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
