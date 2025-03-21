package com.modsen.reportservice.client.driver;

import com.modsen.reportservice.dto.DriverResponseDto;
import com.modsen.reportservice.dto.PageDto;
import com.modsen.reportservice.util.ClientConstants;
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
    public DriverResponseDto getDriverById(String driverId, String bearerToken) {
        log.debug("Getting driver with id {}", driverId);
        return driverClient.getDriverById(driverId, bearerToken);
    }

    @CircuitBreaker(name = ClientConstants.DRIVER_LIST_CLIENT_CIRCUIT)
    @Retry(name = ClientConstants.DRIVER_LIST_CLIENT_RETRY)
    public PageDto<DriverResponseDto> getPageDrivers(int offset, int limit, String bearerToken) {
        log.debug("Getting page drivers");
        return driverClient.getPageDrivers(offset, limit, bearerToken);
    }
}
