package com.modsen.passengerservice.controller;

import com.modsen.passengerservice.dto.PageDto;
import com.modsen.passengerservice.dto.PassengerCreateRequestDto;
import com.modsen.passengerservice.dto.PassengerResponseDto;
import com.modsen.passengerservice.dto.PassengerUpdateRequestDto;
import com.modsen.passengerservice.service.PassengerService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.validation.constraints.Min;

import java.util.UUID;

import static com.modsen.passengerservice.util.AppConstants.UUID_REGEXP;

@RestController
@Validated
@RequestMapping("/api/v1/passengers")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class PassengerController {
    private final PassengerService passengerService;

    @GetMapping
    public PageDto<PassengerResponseDto> getPagePassengers(@RequestParam(defaultValue = "0") @Min(0) Integer offset, @RequestParam (defaultValue = "5")  @Min(1) @Max(20) Integer limit) {
        return passengerService.getPagePassengers(offset, limit);
    }

    @GetMapping("/{id}")
    public PassengerResponseDto getPassenger(@PathVariable String id) {
        return passengerService.getPassengerById(UUID.fromString(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PassengerResponseDto createPassenger(@Valid @RequestBody PassengerCreateRequestDto passengerRequestDTO) {
        return passengerService.addPassenger(passengerRequestDTO);
    }

    @PutMapping("/{id}")
    public PassengerResponseDto updatePassenger(@PathVariable @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}")
                                                    String id,
                                                @Valid @RequestBody PassengerUpdateRequestDto passengerRequestDTO) {
        return passengerService.updatePassenger(UUID.fromString(id), passengerRequestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePassenger(@PathVariable @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}")
                                    String id) {
        passengerService.deletePassenger(UUID.fromString(id));
    }
}
