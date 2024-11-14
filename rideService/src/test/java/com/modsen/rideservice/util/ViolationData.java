package com.modsen.rideservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ViolationData {
    public static final String SOURCE_ADDRESS_MANDATORY = "sourceAddress: Source address is mandatory";
    public static final String DESTINATION_ADDRESS_MANDATORY = "destinationAddress: Destination address is mandatory";
    public static final String PASSENGER_ID_MANDATORY = "passengerId: Passenger id is mandatory";
    public static final String RIDE_STATE_MANDATORY = "rideState: Ride state is mandatory";

    public static final String ID_INVALID = "id: must be greater than or equal to 0";
    public static final String SOURCE_ADDRESS_INVALID = "sourceAddress: size must be between 10 and 255";
    public static final String DESTINATION_ADDRESS_INVALID = "destinationAddress: size must be between 10 and 255";
    public static final String PASSENGER_ID_INVALID = "passengerId: must be greater than or equal to 0";
    public static final String DRIVER_ID_INVALID = "driverId: must be greater than or equal to 0";

    public static final String OFFSET_INSUFFICIENT = "offset: must be greater than or equal to 0";
    public static final String LIMIT_INSUFFICIENT = "limit: must be greater than or equal to 1";
    public static final String LIMIT_EXCEEDED = "limit: must be less than or equal to 20";
}