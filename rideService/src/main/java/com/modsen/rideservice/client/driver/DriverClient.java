package com.modsen.rideservice.client.driver;

import com.modsen.rideservice.dto.DriverResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "${driver.client.name}", path = "${driver.client.path}")
public interface DriverClient {
    @GetMapping("/{id}")
    DriverResponseDto getDriverById(@PathVariable("id") String id,
                                    @RequestHeader("Authorization") String authorization);
}
