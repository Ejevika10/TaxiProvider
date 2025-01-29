package com.modsen.authservice.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="keycloak")
@Getter
@Setter
public class KeycloakConstants {

    private String clientId;

    private String clientSecret;

    private String getTokenUrl;

    private String realmName;

    private String serverUrl;

    private String username;

    private String password;
}
