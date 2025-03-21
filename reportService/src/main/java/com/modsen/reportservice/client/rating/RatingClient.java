package com.modsen.reportservice.client.rating;

import com.modsen.reportservice.dto.RatingResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "${rating.client.name}", path = "${rating.client.path}")
public interface RatingClient {
    @GetMapping("/rides")
    List<RatingResponseDto> getAllRatingsByRideIdIn(@RequestParam List<Long> rideIds,
                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken);
}
