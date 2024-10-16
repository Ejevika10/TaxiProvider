package com.modsen.rideservice.service.impl;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RideCostService {

    private static final Random RANDOM = new Random();

    public Integer getRideCost() {
        int intPart = RANDOM.nextInt(0, 99);
        int fractPart = RANDOM.nextInt(0, 99);
        return intPart * 100 + fractPart;
    }
}
