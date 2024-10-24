package com.modsen.ratingservice.client.ride;

import com.modsen.ratingservice.dto.RideResponseDto;
import com.modsen.ratingservice.util.ClientConstants;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideClientService {
    private final RideClient rideClient;

    @CircuitBreaker(name = ClientConstants.RIDE_CLIENT, fallbackMethod = ClientConstants.RIDE_CLIENT_FALLBACK)
    public RideResponseDto getRideById(long rideId) {
        log.info("getRideById");
        return rideClient.getRideById(rideId);
    }

    private RideResponseDto getRideByIdFallback(Exception e) throws Exception {
        log.info("getRideByIdFallback");
        throw e;
    }
}
