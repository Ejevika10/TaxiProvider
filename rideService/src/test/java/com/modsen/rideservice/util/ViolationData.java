package com.modsen.rideservice.util;

public class ViolationData {
    public static final String sourceAddressMandatory = "sourceAddress: Source address is mandatory";
    public static final String destinationAddressMandatory = "destinationAddress: Destination address is mandatory";
    public static final String passengerIdMandatory = "passengerId: Passenger id is mandatory";
    public static final String rideStateMandatory = "rideState: Ride state is mandatory";

    public static final String idInvalid = "id: must be greater than or equal to 0";
    public static final String sourceAddressInvalid = "sourceAddress: size must be between 10 and 255";
    public static final String destinationAddressInvalid = "destinationAddress: size must be between 10 and 255";
    public static final String passengerIdInvalid = "passengerId: must be greater than or equal to 0";
    public static final String driverIdInvalid = "driverId: must be greater than or equal to 0";

    public static final String offsetInvalid = "offset: must be greater than or equal to 0";
    public static final String limitInvalid = "limit: must be greater than or equal to 1";
    public static final String limitBig = "limit: must be less than or equal to 20";

}
