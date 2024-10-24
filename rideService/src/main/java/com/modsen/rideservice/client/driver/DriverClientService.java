package com.modsen.rideservice.client.driver;

import com.modsen.rideservice.dto.DriverResponseDto;
import com.modsen.rideservice.util.ClientConstants;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverClientService {
    private final DriverClient driverClient;

    @CircuitBreaker(name = ClientConstants.DRIVER_CLIENT, fallbackMethod = ClientConstants.DRIVER_CLIENT_FALLBACK)
    public DriverResponseDto getDriverById(long driverId) {
        log.info("getDriverById");
        return driverClient.getDriverById(driverId);
    }

    private DriverResponseDto getDriverByIdFallback(Exception e) throws Exception {
        log.info("getDriverByIdFallback");
        throw e;
    }
}
