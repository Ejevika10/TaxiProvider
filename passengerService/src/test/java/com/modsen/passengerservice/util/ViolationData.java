package com.modsen.passengerservice.util;

public class ViolationData {

    public static final String passengerNameMandatory = "name: Name is mandatory";
    public static final String passengerEmailMandatory = "email: Email is mandatory";
    public static final String passengerPhoneMandatory = "phone: Phone is mandatory";

    public static final String passengerNameInvalid = "name: size must be between 2 and 50";
    public static final String passengerEmailInvalid = "email: Email is invalid";
    public static final String passengerPhoneInvalid = "phone: Phone is invalid";

    public static final String passengerIdInvalid = "id: must be greater than or equal to 0";
    public static final String offsetInsufficient = "offset: must be greater than or equal to 0";
    public static final String limitInsufficient = "limit: must be greater than or equal to 1";
    public static final String limitExceeded = "limit: must be less than or equal to 20";
}
