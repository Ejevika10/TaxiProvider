package com.modsen.ratingservice.util;

public class ViolationData {

    public static final String userIdMandatory = "userId: User id is mandatory";
    public static final String rideIdMandatory = "rideId: Ride id is mandatory";
    public static final String ratingMandatory = "rating: Rating is mandatory";

    public static final String userIdInvalid = "userId: must be greater than or equal to 0";
    public static final String rideIdInvalid = "rideId: must be greater than or equal to 0";
    public static final String ratingInvalid = "rating: must be less than or equal to 5";

    public static final String offsetInvalid = "offset: must be greater than or equal to 0";
    public static final String limitInvalid = "limit: must be greater than or equal to 1";
    public static final String limitBig = "limit: must be less than or equal to 20";

}
