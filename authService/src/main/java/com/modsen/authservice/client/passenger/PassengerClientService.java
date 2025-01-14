package com.modsen.authservice.client.passenger;

import com.modsen.authservice.dto.PassengerRequestDto;
import com.modsen.authservice.dto.PassengerResponseDto;
import com.modsen.authservice.util.ClientConstants;
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
    public PassengerResponseDto createPassenger(PassengerRequestDto passengerRequestDto, String authorization) {
        log.info("createPassenger");
        return passengerClient.createPassenger(passengerRequestDto, authorization);
    }
}