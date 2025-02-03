package com.modsen.driverservice.controller;

import com.modsen.driverservice.dto.CarRequestDto;
import com.modsen.driverservice.dto.CarResponseDto;
import com.modsen.driverservice.dto.PageDto;
import com.modsen.driverservice.service.CarService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
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

import java.util.UUID;

import static com.modsen.driverservice.util.AppConstants.UUID_REGEXP;

@RestController
@Validated
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class CarController {
    private final CarService carService;

    @GetMapping
    public PageDto<CarResponseDto> getPageCars(@RequestParam (defaultValue = "0") @Min(0) Integer offset, @RequestParam (defaultValue = "5") @Min(1) @Max(20) Integer limit) {
        return carService.getPageCars(offset, limit);
    }

    @GetMapping("/{id}")
    public CarResponseDto getCar(@PathVariable @Min(0) Long id) {
        return carService.getCarById(id);
    }

    @GetMapping("/driver/{driverId}")
    public PageDto<CarResponseDto> getPageCarsByDriverId(@PathVariable @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}")
                                                             String driverId,
                                                      @RequestParam(defaultValue = "0") @Min(0) Integer offset,
                                                      @RequestParam(defaultValue = "5") @Min(1) @Max(20) Integer limit)  {
        return carService.getPageCarsByDriverId(UUID.fromString(driverId), offset, limit);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CarResponseDto createCar(@Valid @RequestBody CarRequestDto carRequestDTO) {
        return carService.addCar(carRequestDTO);
    }

    @PutMapping("/{id}")
    public CarResponseDto updateCar(@PathVariable @Min(0) Long id,@Valid @RequestBody CarRequestDto carRequestDTO) {
        return carService.updateCar(id, carRequestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCar(@PathVariable @Min(0) Long id) {
        carService.deleteCar(id);
    }

}
