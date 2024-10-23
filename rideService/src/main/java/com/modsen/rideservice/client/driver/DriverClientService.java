package com.modsen.rideservice.client.driver;

import com.modsen.rideservice.dto.DriverResponseDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverClientService {
    private final DriverClient driverClient;

    @CircuitBreaker(name = "driver-client", fallbackMethod = "getDriverByIdFallback")
    public DriverResponseDto getDriverById(long driverId) {
        System.out.println("getDriverById!!!");
        return driverClient.getDriverById(driverId);
    }

    private DriverResponseDto getDriverByIdFallback(Exception e) throws Exception {
        System.out.println("fallback!!!");
        throw e;
    }
}
