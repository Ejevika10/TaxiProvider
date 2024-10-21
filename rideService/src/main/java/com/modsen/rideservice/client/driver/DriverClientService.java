package com.modsen.rideservice.client.driver;

import com.modsen.rideservice.dto.DriverResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverClientService {
    private final DriverClient driverClient;

    public DriverResponseDto getDriverById(long driverId) {
        return driverClient.getDriverById(driverId);
    }
}
