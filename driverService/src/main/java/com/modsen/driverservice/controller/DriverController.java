package com.modsen.driverservice.controller;

import com.modsen.driverservice.dto.DriverRequestDto;
import com.modsen.driverservice.dto.DriverResponseDto;
import com.modsen.driverservice.dto.PageDto;
import com.modsen.driverservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {
    private final DriverService driverService;

    @GetMapping
    public PageDto<DriverResponseDto> getPageDrivers(@RequestParam (defaultValue = "0") Integer offset, @RequestParam (defaultValue = "5") Integer limit) {
        return driverService.getPageDrivers(offset, limit);
    }

    @GetMapping("/{id}")
    public DriverResponseDto getDriverById(@PathVariable Long id) {
        return driverService.getDriverById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DriverResponseDto createDriver(@RequestBody DriverRequestDto driverRequestDTO) {
        return driverService.createDriver(driverRequestDTO);
    }

    @PutMapping("/{id}")
    public DriverResponseDto updateDriver(@PathVariable Long id, @RequestBody DriverRequestDto driverRequestDTO) {
        return driverService.updateDriver(id, driverRequestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
    }

}
