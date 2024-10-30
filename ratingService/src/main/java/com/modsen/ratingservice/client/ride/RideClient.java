package com.modsen.ratingservice.client.ride;

import com.modsen.ratingservice.dto.RideResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "${ride.client.name}", path = "${ride.client.path}")
public interface RideClient {
    @GetMapping("/{id}")
    RideResponseDto getRideById(@PathVariable("id") long id);
}
