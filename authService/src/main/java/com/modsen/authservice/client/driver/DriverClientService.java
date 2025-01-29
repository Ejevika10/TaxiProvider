package com.modsen.authservice.client.driver;

import com.modsen.authservice.dto.DriverCreateRequestDto;
import com.modsen.authservice.dto.DriverResponseDto;
import com.modsen.authservice.util.ClientConstants;
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

    @CircuitBreaker(name = ClientConstants.DRIVER_CLIENT_CIRCUIT)
    @Retry(name = ClientConstants.DRIVER_CLIENT_RETRY)
    public DriverResponseDto createDriver(DriverCreateRequestDto driverRequestDto, String authorization) {
        log.info("createDriver");
        return driverClient.createDriver(driverRequestDto, authorization);
    }
}
