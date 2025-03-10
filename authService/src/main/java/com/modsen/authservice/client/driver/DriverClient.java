package com.modsen.authservice.client.driver;

import com.modsen.authservice.dto.DriverCreateRequestDto;
import com.modsen.authservice.dto.DriverResponseDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;

@FeignClient(name = "${driver.client.name}", path = "${driver.client.path}")
public interface DriverClient {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    DriverResponseDto createDriver(@Valid @RequestBody DriverCreateRequestDto driverRequestDTO,
                                   @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken);
}
