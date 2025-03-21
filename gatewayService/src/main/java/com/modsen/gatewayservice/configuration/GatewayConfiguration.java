package com.modsen.gatewayservice.configuration;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class GatewayConfiguration {
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route(r -> r.path("/auth-service/v3/api-docs")
                        .and()
                        .method(HttpMethod.GET)
                        .uri("lb://auth-service"))
                .route(r -> r.path("/driver-service/v3/api-docs")
                        .and()
                        .method(HttpMethod.GET)
                        .uri("lb://driver-service"))
                .route(r -> r.path("/passenger-service/v3/api-docs")
                        .and()
                        .method(HttpMethod.GET)
                        .uri("lb://passenger-service"))
                .route(r -> r.path("/ride-service/v3/api-docs")
                        .and()
                        .method(HttpMethod.GET)
                        .uri("lb://ride-service"))
                .route(r -> r.path("/rating-service/v3/api-docs")
                        .and()
                        .method(HttpMethod.GET)
                        .uri("lb://rating-service"))
                .route(r -> r.path("/report-service/v3/api-docs")
                        .and()
                        .method(HttpMethod.GET)
                        .uri("lb://report-service"))
                .build();
    }
}
