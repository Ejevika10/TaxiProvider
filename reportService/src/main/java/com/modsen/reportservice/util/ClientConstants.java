package com.modsen.reportservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClientConstants {
    public static final String DRIVER_CLIENT_CIRCUIT = "driver-client-circuit";

    public static final String DRIVER_CLIENT_RETRY = "driver-client-retry";

    public static final String RIDE_CLIENT_CIRCUIT = "ride-client-circuit";

    public static final String RIDE_CLIENT_RETRY = "ride-client-retry";

    public static final String RATING_CLIENT_CIRCUIT = "rating-client-circuit";

    public static final String RATING_CLIENT_RETRY = "rating-client-retry";
}
