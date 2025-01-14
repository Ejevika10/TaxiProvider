package com.modsen.authservice.client.passenger;

import com.modsen.authservice.dto.PassengerRequestDto;
import com.modsen.authservice.dto.PassengerResponseDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;

@FeignClient(name = "${passenger.client.name}", path = "${passenger.client.path}")
public interface PassengerClient {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    PassengerResponseDto createPassenger(@Valid @RequestBody PassengerRequestDto passengerRequestDTO,
                                   @RequestHeader("Authorization") String authorization);
}

