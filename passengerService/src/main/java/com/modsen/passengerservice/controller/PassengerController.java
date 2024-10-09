package com.modsen.passengerservice.controller;

import com.modsen.passengerservice.dto.PageDto;
import com.modsen.passengerservice.dto.PassengerRequestDto;
import com.modsen.passengerservice.dto.PassengerResponseDto;
import com.modsen.passengerservice.service.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/passengers")
@RequiredArgsConstructor
public class PassengerController {
    private final PassengerService passengerService;

    @GetMapping
    public PageDto<PassengerResponseDto> getPagePassengers(@RequestParam(defaultValue = "0") Integer offset, @RequestParam (defaultValue = "5") Integer limit) {
        return passengerService.getPagePassengers(offset, limit);
    }

    @GetMapping("/{id}")
    public PassengerResponseDto getPassenger(@PathVariable Long id) {
        return passengerService.getPassengerById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PassengerResponseDto createPassenger(@RequestBody PassengerRequestDto passengerRequestDTO) {
        return passengerService.addPassenger(passengerRequestDTO);
    }

    @PutMapping("/{id}")
    public PassengerResponseDto updatePassenger(@PathVariable Long id, @RequestBody PassengerRequestDto passengerRequestDTO) {
        return passengerService.updatePassenger(id, passengerRequestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePassenger(@PathVariable Long id) {
        passengerService.deletePassenger(id);
    }
}
