package com.modsen.gatewayservice.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;

@Configuration
public class GatewayConfig {

    @Bean
    public GlobalFilter forwardedHeadersFilter() {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .headers(httpHeaders -> {
                        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
                        if (authHeader != null) {
                            httpHeaders.add("Authorization", authHeader);
                        }
                    })
                    .build();
            return chain.filter(exchange.mutate().request(request).build());
        };
    }
}
