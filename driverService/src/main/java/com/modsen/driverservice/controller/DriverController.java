package com.modsen.driverservice.controller;

import com.modsen.driverservice.dto.DriverRequestDto;
import com.modsen.driverservice.dto.DriverResponseDto;
import com.modsen.driverservice.dto.PageDto;
import com.modsen.driverservice.service.DriverService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@Validated
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {
    private final DriverService driverService;

    @GetMapping
    public PageDto<DriverResponseDto> getPageDrivers(@RequestParam (defaultValue = "0") @Min(0) Integer offset, @RequestParam (defaultValue = "5") @Min(1) @Max(20) Integer limit) {
        return driverService.getPageDrivers(offset, limit);
    }

    @GetMapping("/{id}")
    public DriverResponseDto getDriverById(@PathVariable @Min(0) Long id) {
        return driverService.getDriverById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DriverResponseDto createDriver(@Valid @RequestBody DriverRequestDto driverRequestDTO) {
        return driverService.createDriver(driverRequestDTO);
    }

    @PutMapping("/{id}")
    public DriverResponseDto updateDriver(@PathVariable @Min(0) Long id,@Valid @RequestBody DriverRequestDto driverRequestDTO) {
        return driverService.updateDriver(id, driverRequestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDriver(@PathVariable @Min(0) Long id) {
        driverService.deleteDriver(id);
    }

}
