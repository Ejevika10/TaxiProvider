package com.modsen.rideservice.client.passenger;

import com.modsen.rideservice.dto.PassengerResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "${passenger.client.name}", path = "${passenger.client.path}")
public interface PassengerClient {
    @GetMapping("/{id}")
    PassengerResponseDto getPassengerById(@PathVariable("id") long id);
}
