package com.modsen.rideservice.client.passenger;

import com.modsen.rideservice.dto.PassengerResponseDto;
import com.modsen.rideservice.exception.ClientException;
import com.modsen.rideservice.exception.ServiceUnavailableException;
import com.modsen.rideservice.util.AppConstants;
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

    private PassengerResponseDto getPassengerByIdFallback(ClientException e) throws ClientException {
        log.info("getPassengerByIdFallback - ClientException");
        throw e;
    }

    private PassengerResponseDto getPassengerByIdFallback(Exception e) throws Exception {
        log.info("getPassengerByIdFallback - Exception");
        log.info(e.getMessage());
        throw new ServiceUnavailableException(AppConstants.SERVICE_UNAVAILABLE);
    }
}