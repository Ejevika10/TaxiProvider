package com.modsen.authservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {
    public static final String INTERNAL_SERVER_ERROR = "internal.server.error";
    public static final String INVALID_ROLE_VALUE = "Invalid value for User Role";
    public static final String UNKNOWN_ERROR = "Unknown error";

    public static final String SERVICE_UNAVAILABLE = "service.unavailable";

    public static final String ERROR_MESSAGE_FIELD = "errorMessage";

    public static final String PHONE_REGEXP="^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$";

    public static final String ACCESS_TOKEN = "Access-Token";
    public static final String REFRESH_TOKEN = "Refresh-Token";
    public static final String EXPIRES_IN = "Expires-In";

    public static final String GRANT_TYPE_PARAM = "grant_type";
    public static final String CLIENT_ID_PARAM = "client_id";
    public static final String CLIENT_SECRET_PARAM = "client_secret";
    public static final String USERNAME_PARAM = "username";
    public static final String PASSWORD_PARAM = "password";
    public static final String REFRESH_TOKEN_PARAM = "refresh_token";
}
