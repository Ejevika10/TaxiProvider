package com.modsen.driverservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class E2ETestData {

    public static final String URL_DRIVER = "http://localhost:8083/api/v1/drivers";
    public static final String URL_DRIVER_ID = URL_DRIVER + "/{driverId}";
    public static final String URL_CAR = "http://localhost:8083/api/v1/cars";
    public static final String URL_CAR_ID = URL_CAR + "/{carId}";
    public static final String URL_CAR_DRIVER_ID = URL_CAR + "/driver/{driverId}";
}
