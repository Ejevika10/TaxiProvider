package com.modsen.rideservice.client;

import com.modsen.rideservice.dto.DriverResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "driver-service", url = "http://localhost:8083/api/v1/drivers")
public interface DriverClient {
    @GetMapping("/{id}")
    DriverResponseDto getDriverById(@PathVariable("id") Long id);
}
