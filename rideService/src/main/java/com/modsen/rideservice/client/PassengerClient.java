package com.modsen.rideservice.client;

import com.modsen.rideservice.dto.PassengerResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "passenger-service", url = "http://localhost:8082/api/v1/passengers")
public interface PassengerClient {
    @GetMapping("/{id}")
    PassengerResponseDto getPassengerById(@PathVariable("id") Long id);
}
