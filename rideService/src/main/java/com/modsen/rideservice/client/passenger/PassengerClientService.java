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

    @CircuitBreaker(name = ClientConstants.PASSENGER_CLIENT_CIRCUIT, fallbackMethod = ClientConstants.PASSENGER_CLIENT_FALLBACK)
    @Retry(name = ClientConstants.PASSENGER_CLIENT_RETRY)
    public PassengerResponseDto getPassengerById(long passengerId) {
        log.info("getPassengerById");
        return passengerClient.getPassengerById(passengerId);
    }

    private PassengerResponseDto getPassengerByIdFallback(Exception e) throws Exception {
        log.info("getPassengerByIdFallback");
        throw e;
    }
}