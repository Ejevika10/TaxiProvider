package com.modsen.reportservice.client.driver;

import com.modsen.reportservice.dto.DriverResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "${driver.client.name}", path = "${driver.client.path}")
public interface DriverClient {
    @GetMapping("/{id}")
    DriverResponseDto getDriverById(@PathVariable("id") String id,
                                    @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken);

    @GetMapping("/all")
    List<DriverResponseDto> getAllDrivers(@RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken);
}
