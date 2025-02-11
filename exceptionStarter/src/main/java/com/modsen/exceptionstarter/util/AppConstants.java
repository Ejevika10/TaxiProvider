package com.modsen.exceptionstarter.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {
    public static final String INTERNAL_SERVER_ERROR = "internal.server.error";
    public static final String SERVICE_UNAVAILABLE = "service.unavailable";
}