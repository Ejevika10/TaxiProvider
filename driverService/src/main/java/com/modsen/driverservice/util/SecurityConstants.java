package com.modsen.driverservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityConstants {

    public static final String KEYCLOAK_CLIENT_ID = "${keycloak.client-id}";
    public static final String TOKEN_ISSUER_URL = "${keycloak.issuer-url}";

    public static final String CLAIM_REALM_ACCESS = "realm_access";
    public static final String CLAIM_RESOURCE_ACCESS = "resource_access";
    public static final String CLAIM_ROLES = "roles";
}
