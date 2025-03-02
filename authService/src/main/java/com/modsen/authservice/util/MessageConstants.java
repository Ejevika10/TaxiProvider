package com.modsen.authservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageConstants {
    public static final String UNKNOWN_ERROR = "Unknown error";

    public static final String SERVICE_UNAVAILABLE = "service.unavailable";

    public static final String ERROR_MESSAGE_FIELD = "errorMessage";

    public static final String USER_DOESNT_EXIST = "user.doesnt.exist";

    public static final String INVALID_REFRESH_TOKEN = "invalid.refresh.token";

    public static final String INVALID_ROLE_VALUE = "invalid.user.role";
}
