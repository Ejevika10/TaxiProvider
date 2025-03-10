package com.modsen.ratingservice.client.ride;

import com.modsen.ratingservice.dto.RideResponseDto;
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

    @Retry(name = ClientConstants.RIDE_CLIENT_RETRY)
    @CircuitBreaker(name = ClientConstants.RIDE_CLIENT_CIRCUIT)
    public RideResponseDto getRideById(long rideId, String bearerToken) {
        log.info("getRideById");
        return rideClient.getRideById(rideId, bearerToken);
    }
}
