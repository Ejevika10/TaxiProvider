package com.modsen.reportservice.client.ride;

import com.modsen.reportservice.dto.RideResponseDto;
import jakarta.validation.constraints.Pattern;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalDateTime;
import java.util.List;

import static com.modsen.reportservice.util.AppConstants.UUID_REGEXP;

@FeignClient(name = "${ride.client.name}", path = "${ride.client.path}")
public interface RideClient {
    @GetMapping("/driver/{driverId}/{rideDateTime}")
    List<RideResponseDto> getRidesByDriverIdAndLocalDateTime(@PathVariable @Pattern(regexp = UUID_REGEXP) String driverId,
                                                             @PathVariable LocalDateTime rideDateTime,
                                                             @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken);
}
