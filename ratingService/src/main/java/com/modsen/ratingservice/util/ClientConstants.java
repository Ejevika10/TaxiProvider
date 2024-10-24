package com.modsen.ratingservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClientConstants {
    public static final String RIDE_CLIENT_CIRCUIT = "ride-client-circuit";

    public static final String RIDE_CLIENT_CIRCUIT_FALLBACK = "getRideByIdFallback";

    public static final String RIDE_CLIENT_RETRY = "ride-client-retry";

}
