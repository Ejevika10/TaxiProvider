package com.modsen.ratingservice.client.ride;

import com.modsen.ratingservice.dto.RideResponseDto;
import com.modsen.ratingservice.exception.ClientException;
import com.modsen.ratingservice.exception.ServiceUnavailableException;
import com.modsen.ratingservice.util.AppConstants;
import com.modsen.ratingservice.util.ClientConstants;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideClientService {
    private final RideClient rideClient;

    @CircuitBreaker(name = ClientConstants.RIDE_CLIENT_CIRCUIT, fallbackMethod = ClientConstants.RIDE_CLIENT_CIRCUIT_FALLBACK)
    @Retry(name = ClientConstants.RIDE_CLIENT_RETRY)
    public RideResponseDto getRideById(long rideId) {
        log.info("getRideById");
        return rideClient.getRideById(rideId);
    }

    private RideResponseDto getRideByIdFallback(ClientException e) throws ClientException {
        log.info("getRideByIdFallback - ClientException");
        throw e;
    }

    private RideResponseDto getRideByIdFallback(Exception e) throws ServiceUnavailableException {
        log.info("getRideByIdFallback - Exception");
        log.info(e.getMessage());
        throw new ServiceUnavailableException(AppConstants.SERVICE_UNAVAILABLE);
    }
}
