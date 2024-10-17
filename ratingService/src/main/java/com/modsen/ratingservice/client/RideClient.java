package com.modsen.ratingservice.client;

import com.modsen.ratingservice.dto.RideResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ride-service", url = "http://localhost:8081/api/v1/rides")
public interface RideClient {
    @GetMapping("/{id}")
    RideResponseDto getRideById(@PathVariable("id") Long id);
}
