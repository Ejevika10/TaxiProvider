package com.modsen.passengerservice.controller;

import com.modsen.passengerservice.dto.PageDto;
import com.modsen.passengerservice.dto.PassengerRequestDto;
import com.modsen.passengerservice.dto.PassengerResponseDto;
import com.modsen.passengerservice.service.PassengerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.validation.constraints.Min;

@RestController
@Validated
@RequestMapping("/api/v1/passengers")
@RequiredArgsConstructor
public class PassengerController {
    private final PassengerService passengerService;

    @GetMapping
    public PageDto<PassengerResponseDto> getPagePassengers(@RequestParam(defaultValue = "0") @Min(0) Integer offset, @RequestParam (defaultValue = "5")  @Min(1) @Max(20) Integer limit) {
        return passengerService.getPagePassengers(offset, limit);
    }

    @GetMapping("/{id}")
    public PassengerResponseDto getPassenger(@PathVariable @Min(0) Long id) {
        return passengerService.getPassengerById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PassengerResponseDto createPassenger(@Valid @RequestBody PassengerRequestDto passengerRequestDTO) {
        return passengerService.addPassenger(passengerRequestDTO);
    }

    @PutMapping("/{id}")
    public PassengerResponseDto updatePassenger(@PathVariable @Min(0) Long id, @Valid @RequestBody PassengerRequestDto passengerRequestDTO) {
        return passengerService.updatePassenger(id, passengerRequestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePassenger(@PathVariable @Min(0) Long id) {
        passengerService.deletePassenger(id);
    }
}
