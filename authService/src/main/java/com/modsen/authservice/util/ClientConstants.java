package com.modsen.authservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClientConstants {
    public static final String DRIVER_CLIENT_CIRCUIT = "driver-client-circuit";

    public static final String DRIVER_CLIENT_RETRY = "driver-client-retry";

    public static final String PASSENGER_CLIENT_CIRCUIT = "passenger-client-circuit";

    public static final String PASSENGER_CLIENT_RETRY = "passenger-client-retry";
}