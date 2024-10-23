package com.modsen.ratingservice.client.ride;

import com.modsen.ratingservice.dto.RideResponseDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RideClientService {
    private final RideClient rideClient;

    @CircuitBreaker(name = "rating-service", fallbackMethod = "fallbackMethod")
    public RideResponseDto getRideById(long rideId) {
        System.out.println("getRideById!!!");
        return rideClient.getRideById(rideId);
    }

    private RideResponseDto fallbackMethod(Exception e) throws Exception {
        System.out.println("fallback!!!");
        throw e;
    }
}
