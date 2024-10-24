package com.modsen.rideservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClientConstants {
    public static final String DRIVER_CLIENT = "driver-client";

    public static final String DRIVER_CLIENT_FALLBACK = "getDriverByIdFallback";

    public static final String PASSENGER_CLIENT = "passenger-client";

    public static final String PASSENGER_CLIENT_FALLBACK = "getPassengerByIdFallback";


}
