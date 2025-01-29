package com.modsen.rideservice.client.passenger;

import com.modsen.rideservice.dto.PassengerResponseDto;
import com.modsen.rideservice.util.ClientConstants;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassengerClientService {
    private final PassengerClient passengerClient;

    @Retry(name = ClientConstants.PASSENGER_CLIENT_RETRY)
    @CircuitBreaker(name = ClientConstants.PASSENGER_CLIENT_CIRCUIT)
    public PassengerResponseDto getPassengerById(String passengerId, String authorizationToken) {
        log.info("getPassengerById");
        PassengerResponseDto passengerResponse = passengerClient.getPassengerById(passengerId, authorizationToken);
        log.info(passengerResponse.email());
        return passengerResponse;
    }
}