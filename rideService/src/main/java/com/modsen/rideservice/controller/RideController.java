package com.modsen.rideservice.controller;

import com.modsen.rideservice.dto.PageDto;
import com.modsen.rideservice.dto.RideAcceptRequestDto;
import com.modsen.rideservice.dto.RideCreateRequestDto;
import com.modsen.rideservice.dto.RideRequestDto;
import com.modsen.rideservice.dto.RideResponseDto;
import com.modsen.rideservice.dto.RideStateRequestDto;
import com.modsen.rideservice.service.RideService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.modsen.rideservice.util.AppConstants.UUID_REGEXP;

@RestController
@Validated
@RequestMapping("/api/v1/rides")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@Slf4j
public class RideController implements RideEndpoints {
    private final RideService rideService;

    @Override
    @GetMapping
    public PageDto<RideResponseDto> getPageRides(@RequestParam(defaultValue = "0") @Min(0) Integer offset,
                                                 @RequestParam(defaultValue = "5") @Min(1) @Max(20) Integer limit) {
        return rideService.getPageRides(offset, limit);
    }

    @Override
    @GetMapping("/driver/{driverId}")
    public PageDto<RideResponseDto> getPageRidesByDriverId(@PathVariable @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}")
                                                               String driverId,
                                                           @RequestParam(defaultValue = "0") @Min(0) Integer offset,
                                                           @RequestParam(defaultValue = "5") @Min(1) @Max(20) Integer limit) {
        return rideService.getPageRidesByDriverId(UUID.fromString(driverId), offset, limit);
    }

    @Override
    @GetMapping("/passenger/{passengerId}")
    public PageDto<RideResponseDto> getPageRidesByPassengerId(@PathVariable @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}")
                                                                  String passengerId,
                                                              @RequestParam(defaultValue = "0") @Min(0) Integer offset,
                                                              @RequestParam(defaultValue = "5") @Min(1) @Max(20) Integer limit) {
        return rideService.getPageRidesByPassengerId(UUID.fromString(passengerId), offset, limit);
    }

    @Override
    @GetMapping("/{id}")
    public RideResponseDto getRide(@PathVariable @Min(0) Long id) {
        return rideService.getRideById(id);
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RideResponseDto createRide(@Validated @RequestBody RideCreateRequestDto rideRequestDto,
                                      @RequestHeader("Authorization") String authorizationToken) {
        return rideService.createRide(rideRequestDto, authorizationToken);
    }

    @Override
    @PutMapping("/{id}")
    public RideResponseDto updateRide(@PathVariable @Min(0) Long id,
                                      @Validated @RequestBody RideRequestDto rideRequestDto,
                                      @RequestHeader("Authorization") String authorizationToken) {
        return rideService.updateRide(id, rideRequestDto, authorizationToken);
    }

    @Override
    @PutMapping("/{id}/accept")
    public RideResponseDto acceptRide(@PathVariable @Min(0) Long id,
                                      @Validated @RequestBody RideAcceptRequestDto rideRequestDto,
                                      @RequestHeader("Authorization") String authorizationToken) {
        return rideService.acceptRide(id, rideRequestDto, authorizationToken);
    }

    @Override
    @PutMapping("/{id}/cancel")
    public RideResponseDto cancelRide(@PathVariable @Min(0) Long id,
                                      @RequestHeader("Authorization") String authorizationToken) {
        return rideService.cancelRide(id, authorizationToken);
    }

    @Override
    @PutMapping("/{id}/state")
    public RideResponseDto updateRideState(@PathVariable @Min(0) Long id,
                                           @Validated @RequestBody RideStateRequestDto state) {
        return rideService.setNewState(id, state);
    }
}
