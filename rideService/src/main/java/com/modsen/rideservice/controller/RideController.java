package com.modsen.rideservice.controller;

import com.modsen.rideservice.dto.PageDto;
import com.modsen.rideservice.dto.RideRequestDto;
import com.modsen.rideservice.dto.RideResponseDto;
import com.modsen.rideservice.service.RideService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/v1/rides")
@RequiredArgsConstructor
public class RideController {
    private final RideService rideService;

    @GetMapping
    public PageDto<RideResponseDto> getPageRides(@RequestParam(defaultValue = "0") @Min(0) Integer offset, @RequestParam (defaultValue = "5") @Min(1) @Max(20) Integer limit){
        return rideService.getPageRides(offset, limit);
    }

    @GetMapping("/driver/{driverId}")
    public PageDto<RideResponseDto> getPageRidesByDriverId(@Valid @PathVariable Long driverId, @RequestParam(defaultValue = "0") @Min(0) Integer offset, @RequestParam (defaultValue = "5") @Min(1) @Max(20) Integer limit){
        return rideService.getPageRidesByDriverId(driverId, offset, limit);
    }

    @GetMapping("/passenger/{passengerId}")
    public PageDto<RideResponseDto> getPageRidesByPassengerId(@Valid @PathVariable Long passengerId, @RequestParam(defaultValue = "0") @Min(0) Integer offset, @RequestParam (defaultValue = "5") @Min(1) @Max(20) Integer limit){
        return rideService.getPageRidesByPassengerId(passengerId, offset, limit);
    }

    @GetMapping("/{id}")
    public RideResponseDto getRide(@PathVariable @Min(0) Long id) {
        return rideService.getRideById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RideResponseDto createRide(@Valid @RequestBody RideRequestDto rideRequestDto) {
        return rideService.createRide(rideRequestDto);
    }

    @PutMapping("/{id}")
    public RideResponseDto updateRide(@PathVariable @Min(0) Long id, @Valid @RequestBody RideRequestDto rideRequestDto) {
        return rideService.updateRide(id, rideRequestDto);
    }

    @PutMapping("/{id}/state/{state}")
    public RideResponseDto updateRideState(@PathVariable @Min(0) Long id, @PathVariable String state) {
        return rideService.setNewState(id, state);
    }
}
