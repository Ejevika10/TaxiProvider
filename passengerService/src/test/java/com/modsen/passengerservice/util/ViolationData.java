package com.modsen.passengerservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ViolationData {
    public static final String PASSENGER_ID_MANDATORY = "id: Id is mandatory";
    public static final String PASSENGER_NAME_MANDATORY = "name: Name is mandatory";
    public static final String PASSENGER_EMAIL_MANDATORY = "email: Email is mandatory";
    public static final String PASSENGER_PHONE_MANDATORY = "phone: Phone is mandatory";

    public static final String PASSENGER_NAME_INVALID = "name: size must be between 4 and 100";
    public static final String PASSENGER_EMAIL_INVALID = "email: Email is invalid";
    public static final String PASSENGER_PHONE_INVALID = "phone: Phone is invalid";

    public static final String UUID_INVALID = "id: id should have uuid format";
    public static final String OFFSET_INSUFFICIENT = "offset: must be greater than or equal to 0";
    public static final String LIMIT_INSUFFICIENT = "limit: must be greater than or equal to 1";
    public static final String LIMIT_EXCEEDED = "limit: must be less than or equal to 20";
}