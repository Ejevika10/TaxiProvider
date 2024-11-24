package com.modsen.rideservice.util;

import com.modsen.rideservice.dto.DriverResponseDto;
import com.modsen.rideservice.dto.PageDto;
import com.modsen.rideservice.dto.PassengerResponseDto;
import com.modsen.rideservice.dto.RideRequestDto;
import com.modsen.rideservice.dto.RideResponseDto;
import com.modsen.rideservice.dto.RideStateRequestDto;
import com.modsen.rideservice.model.Ride;
import com.modsen.rideservice.model.RideState;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestData {
    public static final Long RIDE_ID = 1L;
    public static final Long INSUFFICIENT_RIDE_ID = -1L;
    public static final String URL_RIDE = "/api/v1/rides";
    public static final String URL_RIDE_ID = URL_RIDE + "/{rideId}";
    public static final String URL_RIDE_ID_STATE = URL_RIDE + "/{rideId}/state";

    public static final String URL_RIDE_DRIVER_ID = URL_RIDE + "/driver/{driverId}";
    public static final String URL_RIDE_PASSENGER_ID = URL_RIDE + "/passenger/{passengerId}";
    public static final String OFFSET = "offset";
    public static final String LIMIT = "limit";
    public static final String PAGE_SIZE = "pageSize";
    public static final String PAGE_NUMBER = "pageNumber";

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

    public static final String URL_DRIVER_ID = "/api/v1/drivers/";
    public static final String URL_PASSENGER_ID = "/api/v1/passengers/";
    public static final String PASSENGER_NOT_FOUND = "passenger not found";
    public static final String DRIVER_NOT_FOUND = "driver not found";
    public static final String PASSENGER_SERVICE_NAME = "passenger-service";
    public static final int PASSENGER_SERVICE_PORT = 8888;
    public static final String DRIVER_SERVICE_NAME = "driver-service";
    public static final int DRIVER_SERVICE_PORT = 8889;

    private static final Integer RIDE_COST = 1000;
    private static final String SOURCE_ADDRESS = "Source address";
    private static final String DESTINATION_ADDRESS = "Destination address";

    private static final Integer INVALID_RIDE_COST = -1000;
    private static final String INVALID_SOURCE_ADDRESS = "addr";
    private static final String INVALID_DESTINATION_ADDRESS = "addr";

    public static final String RIDE_SCRIPT = "ride-controller-preparation.sql";

    public static Ride.RideBuilder getRideBuilder() {
        return Ride.builder()
                .id(RIDE_ID)
                .passengerId(PASSENGER_ID)
                .driverId(DRIVER_ID)
                .sourceAddress(SOURCE_ADDRESS)
                .destinationAddress(DESTINATION_ADDRESS)
                .rideState(RideState.CREATED)
                .rideCost(RIDE_COST)
                .rideDateTime(LocalDateTime.now());
    }

    public static Ride getRide() {
        return getRideBuilder().build();
    }

    public static RideRequestDto.RideRequestDtoBuilder getRideRequestDtoBuilder() {
        return RideRequestDto.builder()
                .passengerId(PASSENGER_ID)
                .driverId(DRIVER_ID)
                .sourceAddress(SOURCE_ADDRESS)
                .destinationAddress(DESTINATION_ADDRESS)
                .rideState(RideState.CREATED)
                .rideCost(RIDE_COST)
                .rideDateTime(LocalDateTime.now());
    }

    public static RideRequestDto getRideRequestDto() {
        return getRideRequestDtoBuilder().build();
    }

    public static RideRequestDto getInvalidRideRequestDto() {
        return getRideRequestDtoBuilder()
                .passengerId(INSUFFICIENT_PASSENGER_ID)
                .driverId(INSUFFICIENT_DRIVER_ID)
                .sourceAddress(INVALID_SOURCE_ADDRESS)
                .destinationAddress(INVALID_DESTINATION_ADDRESS)
                .rideState(null)
                .rideCost(INVALID_RIDE_COST)
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
                .sourceAddress(SOURCE_ADDRESS)
                .destinationAddress(DESTINATION_ADDRESS)
                .rideState(RideState.CREATED)
                .rideCost(RIDE_COST)
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

    public static PageDto<RideResponseDto> getPageRideResponseDto() {
        return new PageDto<>(
                OFFSET_VALUE,
                LIMIT_VALUE,
                1,
                1,
                getRideResponseDtoList()
        );
    }

    public static DriverResponseDto getDriverResponseDto() {
        return new DriverResponseDto(1L, "Driver", "driver@mail.ru", "712345678");
    }

    public static PassengerResponseDto getPassengerResponseDto() {
        return new PassengerResponseDto(1L, "Passenger", "passenger@mail.ru", "712345678");

    }
}