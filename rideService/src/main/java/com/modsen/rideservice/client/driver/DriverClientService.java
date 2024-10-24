package com.modsen.rideservice.client.driver;

import com.modsen.rideservice.dto.DriverResponseDto;
import com.modsen.rideservice.util.ClientConstants;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverClientService {
    private final DriverClient driverClient;

    @CircuitBreaker(name = ClientConstants.DRIVER_CLIENT_CIRCUIT, fallbackMethod = ClientConstants.DRIVER_CLIENT_FALLBACK)
    @Retry(name = ClientConstants.DRIVER_CLIENT_RETRY)
    public DriverResponseDto getDriverById(long driverId) {
        log.info("getDriverById");
        return driverClient.getDriverById(driverId);
    }

    private DriverResponseDto getDriverByIdFallback(Exception e) throws Exception {
        log.info("getDriverByIdFallback");
        throw e;
    }
}
