package com.modsen.rideservice.client.driver;

import com.modsen.rideservice.dto.DriverResponseDto;
import com.modsen.rideservice.exception.ClientException;
import com.modsen.rideservice.exception.ServiceUnavailableException;
import com.modsen.rideservice.util.AppConstants;
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

    private DriverResponseDto getDriverByIdFallback(ClientException e) throws ClientException {
        log.info("getDriverByIdFallback - ClientException");
        throw e;
    }

    private DriverResponseDto getDriverByIdFallback(Exception e) throws Exception {
        log.info("getDriverByIdFallback - Exception");
        log.info(e.getMessage());
        throw new ServiceUnavailableException(AppConstants.SERVICE_UNAVAILABLE);
    }
}
