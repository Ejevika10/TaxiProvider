package com.modsen.authservice.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class KeycloakConfiguration {

    private final KeycloakProperties keycloakProperties;

    @Bean
    public Keycloak keycloak() {
        log.info(keycloakProperties.getServerUrl());
        return KeycloakBuilder.builder()
            .serverUrl(keycloakProperties.getServerUrl())
            .realm(keycloakProperties.getRealmName())
            .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
            .clientId(keycloakProperties.getClientId())
            .clientSecret(keycloakProperties.getClientSecret())
            .username(keycloakProperties.getUsername())
            .password(keycloakProperties.getPassword())
            .build();
    }
}
