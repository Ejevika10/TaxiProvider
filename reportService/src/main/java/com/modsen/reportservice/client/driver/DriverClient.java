package com.modsen.reportservice.client.driver;

import com.modsen.reportservice.dto.DriverResponseDto;
import com.modsen.reportservice.dto.PageDto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${driver.client.name}", path = "${driver.client.path}")
public interface DriverClient {
    @GetMapping("/{id}")
    DriverResponseDto getDriverById(@PathVariable("id") String id,
                                    @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken);

    @GetMapping
    PageDto<DriverResponseDto> getPageDrivers(@RequestParam(defaultValue = "0") @Min(0) Integer offset,
                                              @RequestParam (defaultValue = "5") @Min(1) @Max(20) Integer limit,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken);
}
