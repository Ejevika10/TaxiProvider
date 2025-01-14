package com.modsen.authservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KeycloakConstants {

    //@Value("${keycloak.client-id}")
    public final static String CLIENT_ID = "auth-service";

    //@Value("${keycloak.client-secret}")
    public static String CLIENT_SECRET = "JRq0FghgG7N5h0mhPE7rTK15h3CrBbXF";

    //@Value("${keycloak.get-token-url}")
    public static String GET_TOKEN_URL = "http://localhost:8484/realms/taxi-provider-realm/protocol/openid-connect/token";

    public final static String REALM_NAME = "taxi-provider-realm";

    public final static String SERVER_URL = "http://localhost:8484";

    //@Value("${keycloak.username}")
    public static String USERNAME = "admin";

    //@Value("${keycloak.password}")
    public static String PASSWORD = "admin";

    public static final String GRANT_TYPE_PASSWORD = "password";
    public static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
    public static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

    public static final String DRIVER_ROLE = "driver";
    public static final String PASSENGER_ROLE = "passenger";
}
