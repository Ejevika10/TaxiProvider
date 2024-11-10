package com.modsen.driverservice.util;

public class ViolationData {
    public static final String carModelMandatory = "model: Model is mandatory";
    public static final String carColorMandatory = "color: Color is mandatory";
    public static final String carBrandMandatory = "brand: Brand is mandatory";
    public static final String carNumberMandatory = "number: Number is mandatory";

    public static final String carModelInvalid = "model: size must be between 2 and 50";
    public static final String carColorInvalid = "color: size must be between 2 and 50";
    public static final String carBrandInvalid = "brand: size must be between 2 and 50";
    public static final String carNumberInvalid = "number: size must be between 2 and 20";
    public static final String driverIdInvalid = "driverId: must be greater than or equal to 0";

    public static final String driverNameMandatory = "name: Name is mandatory";
    public static final String driverEmailMandatory = "email: Email is mandatory";
    public static final String driverPhoneMandatory = "phone: Phone is mandatory";

    public static final String driverNameInvalid = "name: size must be between 4 and 100";
    public static final String driverEmailInvalid = "email: Email is invalid";
    public static final String driverPhoneInvalid = "phone: Phone is invalid";

    public static final String idInvalid = "id: must be greater than or equal to 0";
    public static final String offsetInsufficient = "offset: must be greater than or equal to 0";
    public static final String limitInsufficient = "limit: must be greater than or equal to 1";
    public static final String limitExceeded = "limit: must be less than or equal to 20";
}
