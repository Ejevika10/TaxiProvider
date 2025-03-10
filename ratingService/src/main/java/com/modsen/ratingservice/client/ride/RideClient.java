package com.modsen.ratingservice.client.ride;

import com.modsen.ratingservice.dto.RideResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "${ride.client.name}", path = "${ride.client.path}")
public interface RideClient {
    @GetMapping("/{id}")
    RideResponseDto getRideById(@PathVariable("id") long id,
                                @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken);
}
