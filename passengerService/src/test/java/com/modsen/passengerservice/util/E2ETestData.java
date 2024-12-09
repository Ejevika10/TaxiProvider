package com.modsen.passengerservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class E2ETestData {

    public static final String URL_PASSENGER = "http://localhost:8082/api/v1/passengers";
    public static final String URL_PASSENGER_ID = URL_PASSENGER + "/{passengerId}";
}
