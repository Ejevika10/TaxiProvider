package com.modsen.rideservice.client.passenger;

import com.modsen.rideservice.dto.PassengerResponseDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PassengerClientService {
    private final PassengerClient passengerClient;

    @CircuitBreaker(name = "passenger-client", fallbackMethod = "getPassengerByIdFallback")
    public PassengerResponseDto getPassengerById(long passengerId) {
        System.out.println("getPassengerById!!!");
        return passengerClient.getPassengerById(passengerId);
    }

    private PassengerResponseDto getPassengerByIdFallback(Exception e) throws Exception {
        System.out.println("fallback!!!");
        throw e;
    }
}