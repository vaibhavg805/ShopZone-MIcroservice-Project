package com.example.apiGatewayApplication.ApiGatewayApplication.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class InternalSecretFilter implements GlobalFilter, Ordered {

    @Value("${internal.secret}")
    private String secret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Running Internal Filter...");
        ServerHttpRequest mutated = exchange.getRequest().mutate()
                .header("X-Internal-Secret", secret)
                .build();
        log.info("Inside internal filter, to block gateway bypass");
        return chain.filter(exchange.mutate().request(mutated).build());
    }

    @Override
    public int getOrder() { return -2; }
}
