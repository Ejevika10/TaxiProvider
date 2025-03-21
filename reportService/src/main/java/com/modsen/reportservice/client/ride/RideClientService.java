package com.modsen.reportservice.client.ride;

import com.modsen.reportservice.dto.RideResponseDto;
import com.modsen.reportservice.util.ClientConstants;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideClientService {
    private final RideClient rideClient;

    @Retry(name = ClientConstants.RIDE_CLIENT_RETRY)
    @CircuitBreaker(name = ClientConstants.RIDE_CLIENT_CIRCUIT)
    public List<RideResponseDto> getRidesByDriverIdAndLocalDateTime(String driverId, LocalDateTime rideDateTime, String bearerToken) {
        log.debug("Getting rides by driverId {} and after date {}", driverId, rideDateTime);
        return rideClient.getRidesByDriverIdAndLocalDateTime(driverId, rideDateTime, bearerToken);
    }
}
