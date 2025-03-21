package com.modsen.reportservice.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="keycloak")
@Getter
@Setter
public class KeycloakProperties {

    private String clientId;

    private String clientSecret;

    private String getTokenUrl;
}
