package com.modsen.authservice.configuration;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static com.modsen.authservice.util.KeycloakConstants.CLIENT_ID;
import static com.modsen.authservice.util.KeycloakConstants.CLIENT_SECRET;
import static com.modsen.authservice.util.KeycloakConstants.GRANT_TYPE_CLIENT_CREDENTIALS;
import static com.modsen.authservice.util.KeycloakConstants.PASSWORD;
import static com.modsen.authservice.util.KeycloakConstants.REALM_NAME;
import static com.modsen.authservice.util.KeycloakConstants.SERVER_URL;
import static com.modsen.authservice.util.KeycloakConstants.USERNAME;

@Configuration
@Slf4j
public class KeycloakConfiguration {
    @Bean
    public Keycloak keycloak() {
        log.info(SERVER_URL);
        return KeycloakBuilder.builder()
            .serverUrl(SERVER_URL)
            .realm(REALM_NAME)
            .grantType(GRANT_TYPE_CLIENT_CREDENTIALS)
            .clientId(CLIENT_ID)
            .clientSecret(CLIENT_SECRET)
            .username(USERNAME)
            .password(PASSWORD)
            .build();
    }
}
