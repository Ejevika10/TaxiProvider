package com.modsen.authservice.configuration;

import com.modsen.authservice.util.KeycloakConstants;
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

    private final KeycloakConstants keycloakConstants;

    @Bean
    public Keycloak keycloak() {
        log.info(keycloakConstants.getServerUrl());
        return KeycloakBuilder.builder()
            .serverUrl(keycloakConstants.getServerUrl())
            .realm(keycloakConstants.getRealmName())
            .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
            .clientId(keycloakConstants.getClientId())
            .clientSecret(keycloakConstants.getClientSecret())
            .username(keycloakConstants.getUsername())
            .password(keycloakConstants.getPassword())
            .build();
    }
}
