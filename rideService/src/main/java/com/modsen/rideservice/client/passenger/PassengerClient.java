package com.modsen.rideservice.client.passenger;

import com.modsen.rideservice.dto.PassengerResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "${passenger.client.name}", path = "${passenger.client.path}")
public interface PassengerClient {
    @GetMapping("/{id}")
    PassengerResponseDto getPassengerById(@PathVariable("id") String id,
                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken);
}
