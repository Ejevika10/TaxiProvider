package com.modsen.rideservice.util;

import com.modsen.rideservice.dto.DriverResponseDto;
import com.modsen.rideservice.dto.PassengerResponseDto;
import com.modsen.rideservice.dto.RideRequestDto;
import com.modsen.rideservice.dto.RideResponseDto;
import com.modsen.rideservice.dto.RideStateRequestDto;
import com.modsen.rideservice.model.Ride;
import com.modsen.rideservice.model.RideState;

import java.time.LocalDateTime;
import java.util.List;

public class TestData {
    public static final Long RIDE_ID = 1L;
    public static final Long INSUFFICIENT_RIDE_ID = -1L;
    public static final String URL_RIDE = "/api/v1/rides";
    public static final String URL_RIDE_ID = URL_RIDE + "/{rideId}";
    public static final String URL_RIDE_ID_STATE = URL_RIDE + "/{rideId}/state";

    public static final String URL_RIDE_DRIVER_ID = URL_RIDE + "/driver/{driverId}";
    public static final String URL_RIDE_PASSENGER_ID = URL_RIDE + "/passenger/{passengerId}";
    public static final String OFFSET = "offset";
    public static final String LIMIT = "limit";

    public static final Integer OFFSET_VALUE = 0;
    public static final Integer LIMIT_VALUE = 5;
    public static final Integer INSUFFICIENT_OFFSET_VALUE = -1;
    public static final Integer INSUFFICIENT_LIMIT_VALUE = -1;
    public static final Integer EXCEEDED_OFFSET_VALUE = 100;
    public static final Integer EXCEEDED_LIMIT_VALUE = 100;

    public static final Long DRIVER_ID = 1L;
    public static final Long INSUFFICIENT_DRIVER_ID = -1L;
    public static final Long PASSENGER_ID = 1L;
    public static final Long INSUFFICIENT_PASSENGER_ID = -1L;

    private static final Integer rideCost = 1000;
    private static final String sourceAddress = "Source address";
    private static final String destinationAddress = "Destination address";

    private static final Integer invalidRideCost = -1000;
    private static final String invalidSourceAddress = "addr";
    private static final String invalidDestinationAddress = "addr";

    public static Ride.RideBuilder getRideBuilder() {
        return Ride.builder()
                .id(RIDE_ID)
                .passengerId(PASSENGER_ID)
                .driverId(DRIVER_ID)
                .sourceAddress(sourceAddress)
                .destinationAddress(destinationAddress)
                .rideState(RideState.CREATED)
                .rideCost(rideCost)
                .rideDateTime(LocalDateTime.now());
    }

    public static Ride getRide() {
        return getRideBuilder().build();
    }

    public static RideRequestDto.RideRequestDtoBuilder getRideRequestDtoBuilder() {
        return RideRequestDto.builder()
                .passengerId(PASSENGER_ID)
                .driverId(DRIVER_ID)
                .sourceAddress(sourceAddress)
                .destinationAddress(destinationAddress)
                .rideState(RideState.CREATED)
                .rideCost(rideCost)
                .rideDateTime(LocalDateTime.now());
    }

    public static RideRequestDto getRideRequestDto() {
        return getRideRequestDtoBuilder().build();
    }

    public static RideRequestDto getInvalidRideRequestDto() {
        return getRideRequestDtoBuilder()
                .passengerId(INSUFFICIENT_PASSENGER_ID)
                .driverId(INSUFFICIENT_DRIVER_ID)
                .sourceAddress(invalidSourceAddress)
                .destinationAddress(invalidDestinationAddress)
                .rideState(null)
                .rideCost(invalidRideCost)
                .rideDateTime(null)
                .build();
    }

    public static RideRequestDto getEmptyRideRequestDto() {
        return getRideRequestDtoBuilder()
                .passengerId(null)
                .driverId(null)
                .sourceAddress(null)
                .destinationAddress(null)
                .rideState(null)
                .rideCost(null)
                .rideDateTime(null)
                .build();
    }

    public static RideResponseDto.RideResponseDtoBuilder getRideResponseDtoBuilder() {
        return RideResponseDto.builder()
                .id(RIDE_ID)
                .passengerId(PASSENGER_ID)
                .driverId(DRIVER_ID)
                .sourceAddress(sourceAddress)
                .destinationAddress(destinationAddress)
                .rideState(RideState.CREATED)
                .rideCost(rideCost)
                .rideDateTime(LocalDateTime.now());
    }

    public static RideResponseDto getRideResponseDto() {
        return getRideResponseDtoBuilder().build();
    }

    public static RideStateRequestDto.RideStateRequestDtoBuilder getRideStateRequestDtoBuilder() {
        return RideStateRequestDto.builder()
                .rideState("accepted");
    }

    public static RideStateRequestDto getRideStateRequestDto() {
        return getRideStateRequestDtoBuilder().build();
    }

    public static List<Ride> getRideList() {
        return List.of(getRide());
    }

    public static List<RideResponseDto> getRideResponseDtoList() {
        return List.of(getRideResponseDto());
    }

    public static DriverResponseDto getDriverResponseDto() {
        return new DriverResponseDto(1L, "Driver", "driver@mail.ru", "712345678");
    }

    public static PassengerResponseDto getPassengerResponseDto() {
        return new PassengerResponseDto(1L, "Passenger", "passenger@mail.ru", "712345678");

    }
}
