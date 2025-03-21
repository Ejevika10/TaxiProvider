package com.modsen.reportservice.client.rating;

import com.modsen.reportservice.dto.RatingResponseDto;
import com.modsen.reportservice.util.ClientConstants;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingClientService {
    private final RatingClient ratingClient;

    @CircuitBreaker(name = ClientConstants.RATING_CLIENT_CIRCUIT)
    @Retry(name = ClientConstants.RATING_CLIENT_RETRY)
    public List<RatingResponseDto> getAllRatingsByRideIdIn(List<Long> rideIds, String bearerToken) {
        log.debug("Getting all rating by rideId in {}", rideIds);
        return ratingClient.getAllRatingsByRideIdIn(rideIds, bearerToken);
    }
}
