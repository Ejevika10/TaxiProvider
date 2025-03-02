package com.modsen.driverservice.controller;

import com.modsen.driverservice.dto.CarRequestDto;
import com.modsen.driverservice.dto.CarResponseDto;
import com.modsen.driverservice.dto.PageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import static com.modsen.driverservice.util.AppConstants.UUID_REGEXP;

@Validated
@Tag(
        name = "Car controller",
        description = """
                The car controller handles operations with car:
                create a new car, update or delete existing car,
                get car by car id or get collection of cars by car's driver id.
                """
)
public interface CarEndpoints {
    @Operation(
            summary = "Get cars",
            description = "Allows users to obtain a collection of cars with pagination."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid")
    })
    PageDto<CarResponseDto> getPageCars(@RequestParam(defaultValue = "0") @Min(0) Integer offset, @RequestParam (defaultValue = "5") @Min(1) @Max(20) Integer limit);

    @Operation(
            summary = "Get car by id",
            description = "Allows users to get a car by id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "404", description = "Car with this id doesn't exist")
    })
    CarResponseDto getCar(@PathVariable @Min(0) Long id);

    @Operation(
            summary = "Get cars by driver id",
            description = "Allows users to obtain a collection of cars by driver id with pagination."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "404", description = "Driver with this id doesn't exist")
    })
    PageDto<CarResponseDto> getPageCarsByDriverId(@PathVariable @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}")
                                                         String driverId,
                                                         @RequestParam(defaultValue = "0") @Min(0) Integer offset,
                                                         @RequestParam(defaultValue = "5") @Min(1) @Max(20) Integer limit);

    @Operation(
            summary = "Create car",
            description = """
                    Allows driver to create a car for itself.
                    When request complete, a car will be created
                    and the user will receive a car info.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "404", description = "Driver with this id doesn't exist"),
            @ApiResponse(responseCode = "409", description = "Your request has conflicts")
    })
    CarResponseDto createCar(@Valid @RequestBody CarRequestDto carRequestDTO);

    @Operation(
            summary = "Update car by id",
            description = """
                    Allows driver to update its car by id.
                    When request complete, a car will be updated
                    and the user will receive a car info.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "404", description = "Driver with this id doesn't exist"),
            @ApiResponse(responseCode = "404", description = "Car with this id doesn't exist"),
            @ApiResponse(responseCode = "409", description = "Your request has conflicts")
    })
    CarResponseDto updateCar(@PathVariable @Min(0) Long id,@Valid @RequestBody CarRequestDto carRequestDTO);

    @Operation(
            summary = "Delete car by id",
            description = """
                    Allows driver to delete its car by id.
                    When request complete, a car will be deleted.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Request completed"),
            @ApiResponse(responseCode = "404", description = "Car with this id doesn't exist")
    })
    void deleteCar(@PathVariable @Min(0) Long id);
}
