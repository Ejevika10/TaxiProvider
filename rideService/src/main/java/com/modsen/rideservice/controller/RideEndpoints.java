package com.modsen.rideservice.controller;

import com.modsen.rideservice.dto.PageDto;
import com.modsen.rideservice.dto.RideAcceptRequestDto;
import com.modsen.rideservice.dto.RideCreateRequestDto;
import com.modsen.rideservice.dto.RideRequestDto;
import com.modsen.rideservice.dto.RideResponseDto;
import com.modsen.rideservice.dto.RideStateRequestDto;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

import static com.modsen.rideservice.util.AppConstants.UUID_REGEXP;

@Validated
@Tag(
        name = "Ride controller",
        description = """
                The ride controller handles operations with rides:
                create ride, update existing ride, manage ride states,
                fet ride by id, get collection of rides by driver id or by passenger id.
                """
)
public interface RideEndpoints {
    @Operation(
            summary = "Get rides",
            description = "Allows users to obtain a collection of rides with pagination."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "401", description = "You have to log in or register")
    })
    PageDto<RideResponseDto> getPageRides(@RequestParam(defaultValue = "0") @Min(0) Integer offset,
                                          @RequestParam(defaultValue = "5") @Min(1) @Max(20) Integer limit);

    @Operation(
            summary = "Get rides by driver id",
            description = "Allows users to obtain a collection of rides by driver id with pagination."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "401", description = "You have to log in or register"),
            @ApiResponse(responseCode = "403", description = "You have no rights to access this resource"),
            @ApiResponse(responseCode = "404", description = "Driver with this id doesn't exist")
    })
    PageDto<RideResponseDto> getPageRidesByDriverId(@PathVariable @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}") String driverId,
                                                    @RequestParam(defaultValue = "0") @Min(0) Integer offset,
                                                    @RequestParam(defaultValue = "5") @Min(1) @Max(20) Integer limit);

    @Hidden
    List<RideResponseDto> getRidesByDriverIdAndRideDateTime(@PathVariable @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}") String driverId,
                                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                            @PathVariable LocalDateTime rideDatetime);

    @Operation(
            summary = "Get rides by passenger id",
            description = "Allows users to obtain a collection of rides by passenger id with pagination."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "401", description = "You have to log in or register"),
            @ApiResponse(responseCode = "403", description = "You have no rights to access this resource"),
            @ApiResponse(responseCode = "404", description = "Passenger with this id doesn't exist")
    })
    PageDto<RideResponseDto> getPageRidesByPassengerId(@PathVariable @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}") String passengerId,
                                                       @RequestParam(defaultValue = "0") @Min(0) Integer offset,
                                                       @RequestParam(defaultValue = "5") @Min(1) @Max(20) Integer limit);

    @Operation(
            summary = "Get ride by id",
            description = "Allows users to get a ride by id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "401", description = "You have to log in or register"),
            @ApiResponse(responseCode = "403", description = "You have no rights to access this resource"),
            @ApiResponse(responseCode = "404", description = "Ride with this id doesn't exist")
    })
    RideResponseDto getRide(@PathVariable @Min(0) Long id);

    @Operation(
            summary = "Create ride",
            description = """
                    Allows passenger to create a ride.
                    When request complete, a ride will be created
                    and the user will receive a ride info.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "401", description = "You have to log in or register"),
            @ApiResponse(responseCode = "403", description = "You have no rights to access this resource"),
            @ApiResponse(responseCode = "404", description = "Passenger with this id doesn't exist"),
            @ApiResponse(responseCode = "409", description = "Your request has conflicts")
    })
    RideResponseDto createRide(@Validated @RequestBody RideCreateRequestDto rideRequestDto,
                                      @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken);

    @Operation(
            summary = "Update ride",
            description = """
                    Allows passenger and driver in ride to update it.
                    When request complete, a ride will be updated
                    and the user will receive a ride info.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "401", description = "You have to log in or register"),
            @ApiResponse(responseCode = "403", description = "You have no rights to access this resource"),
            @ApiResponse(responseCode = "404", description = "Ride with this id doesn't exist"),
            @ApiResponse(responseCode = "404", description = "Passenger with this id doesn't exist"),
            @ApiResponse(responseCode = "404", description = "Driver with this id doesn't exist"),
            @ApiResponse(responseCode = "409", description = "Your request has conflicts")
    })
    RideResponseDto updateRide(@PathVariable @Min(0) Long id,
                                      @Validated @RequestBody RideRequestDto rideRequestDto,
                                      @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken);

    @Operation(
            summary = "Accept ride",
            description = """
                    Allows driver to accept created ride.
                    When request complete, a ride state will be updated
                    and driver id will be set. User will receive a ride info.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "401", description = "You have to log in or register"),
            @ApiResponse(responseCode = "403", description = "You have no rights to access this resource"),
            @ApiResponse(responseCode = "404", description = "Ride with this id doesn't exist"),
            @ApiResponse(responseCode = "404", description = "Driver with this id doesn't exist"),
            @ApiResponse(responseCode = "409", description = "Your request has conflicts")
    })
    RideResponseDto acceptRide(@PathVariable @Min(0) Long id,
                               @Validated @RequestBody RideAcceptRequestDto rideRequestDto,
                               @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken);

    @Operation(
            summary = "Cancel ride",
            description = """
                    Allows driver and passenger in ride to cancel it.
                    When request complete, a ride state will be updated.
                    User will receive a ride info.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "401", description = "You have to log in or register"),
            @ApiResponse(responseCode = "403", description = "You have no rights to access this resource"),
            @ApiResponse(responseCode = "404", description = "Ride with this id doesn't exist"),
            @ApiResponse(responseCode = "409", description = "Your request has conflicts")
    })
    RideResponseDto cancelRide(@PathVariable @Min(0) Long id,
                               @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken);

    @Operation(
            summary = "Update ride state",
            description = """
                    Allows driver in ride to change its state.
                    When request complete, a ride state will be updated.
                    User will receive a ride info.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "401", description = "You have to log in or register"),
            @ApiResponse(responseCode = "403", description = "You have no rights to access this resource"),
            @ApiResponse(responseCode = "404", description = "Ride with this id doesn't exist"),
            @ApiResponse(responseCode = "409", description = "Your request has conflicts")
    })
    RideResponseDto updateRideState(@PathVariable @Min(0) Long id,
                                    @Validated @RequestBody RideStateRequestDto state);

}
