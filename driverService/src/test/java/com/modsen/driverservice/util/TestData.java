package com.modsen.driverservice.util;

import com.modsen.driverservice.dto.CarRequestDto;
import com.modsen.driverservice.dto.CarResponseDto;
import com.modsen.driverservice.dto.DriverRequestDto;
import com.modsen.driverservice.dto.DriverResponseDto;
import com.modsen.driverservice.dto.PageDto;
import com.modsen.driverservice.dto.UserRatingDto;
import com.modsen.driverservice.model.Car;
import com.modsen.driverservice.model.Driver;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestData {

    public static final String URL_CAR = "/api/v1/cars";
    public static final String URL_CAR_ID = URL_CAR + "/{carId}";
    public static final String URL_CAR_DRIVER_ID = URL_CAR + "/driver/{driverId}";

    public static final String URL_DRIVER = "/api/v1/drivers";
    public static final String URL_DRIVER_ID = URL_DRIVER + "/{driverId}";
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

    public static final Long CAR_ID = 1L;
    private static final String COLOR = "red";
    private static final String MODEL = "sedan";
    private static final String BRAND = "audi";
    private static final String NUMBER = "12345";
    public static final Long DRIVER_ID = 1L;

    public static final Long INSUFFICIENT_CAR_ID = -1L;
    private static final String INVALID_COLOR = "r";
    private static final String INVALID_MODEL = "s";
    private static final String INVALID_BRAND = "a";
    private static final String INVALID_NUMBER = "1";
    public static final Long INSUFFICIENT_DRIVER_ID = -1L;

    private static final String NAME = "Driver";
    private static final String EMAIL = "driver@email.com";
    private static final String PHONE = "71234567890";
    private static final Double RATING = 0.0;
    public static final Double NEW_RATING = 5.0;

    private static final String INVALID_NAME = "d";
    private static final String INVALID_EMAIL = "driver";
    private static final String INVALID_PHONE = "11";
    private static final Double INVALID_RATING = -1.0;

    public static final String UNIQUE_NUMBER = "11111";
    public static final String UNIQUE_EMAIL = "driver_unique@email.com";

    public static final String DRIVER_SCRIPT = "driver-controller-preparation.sql";
    public static final String CAR_SCRIPT = "car-controller-preparation.sql";

    public static Car.CarBuilder getCarBuilder() {
        return Car.builder()
                .id(CAR_ID)
                .color(COLOR)
                .model(MODEL)
                .brand(BRAND)
                .number(NUMBER)
                .driver(getDriver())
                .deleted(false);
    }

    public static Car getCar() {
        return getCarBuilder().build();
    }

    public static CarRequestDto.CarRequestDtoBuilder getCarRequestDtoBuilder() {
        return CarRequestDto.builder()
                .brand(BRAND)
                .color(COLOR)
                .model(MODEL)
                .number(NUMBER)
                .driverId(DRIVER_ID);
    }

    public static CarRequestDto getCarRequestDto() {
        return getCarRequestDtoBuilder().build();
    }

    public static CarRequestDto getEmptyCarRequestDto() {
        return getCarRequestDtoBuilder()
                .brand(null)
                .color(null)
                .model(null)
                .number(null)
                .driverId(null)
                .build();
    }

    public static CarRequestDto getInvalidCarRequestDto() {
        return getCarRequestDtoBuilder()
                .brand(INVALID_BRAND)
                .color(INVALID_COLOR)
                .model(INVALID_MODEL)
                .number(INVALID_NUMBER)
                .driverId(INSUFFICIENT_DRIVER_ID)
                .build();
    }

    public static CarResponseDto.CarResponseDtoBuilder getCarResponseDtoBuilder() {
        return CarResponseDto.builder()
                .id(CAR_ID)
                .brand(BRAND)
                .color(COLOR)
                .model(MODEL)
                .number(NUMBER)
                .driver(getDriverResponseDto());
    }

    public static CarResponseDto getCarResponseDto() {
        return getCarResponseDtoBuilder().build();
    }

    public static List<Car> getCarList() {
        return List.of(getCar());
    }

    public static List<CarResponseDto> getCarResponseDtoList() {
        return List.of(getCarResponseDto());
    }

    public static PageDto<CarResponseDto> getPageCarResponseDto() {
        return new PageDto<>(
                OFFSET_VALUE,
                LIMIT_VALUE,
                1,
                1,
                getCarResponseDtoList()
        );
    }

    public static PageDto<DriverResponseDto> getPageDriverResponseDto() {
        return new PageDto<>(
                OFFSET_VALUE,
                LIMIT_VALUE,
                1,
                1,
                getDriverResponseDtoList()
        );
    }

    public static Driver.DriverBuilder getDriverBuilder() {
        return Driver.builder()
                .id(DRIVER_ID)
                .name(NAME)
                .email(EMAIL)
                .phone(PHONE)
                .rating(RATING)
                .cars(null)
                .deleted(false);
    }

    public static Driver getDriver() {
        return getDriverBuilder().build();
    }

    public static DriverRequestDto.DriverRequestDtoBuilder getDriverRequestDtoBuilder() {
        return DriverRequestDto.builder()
                .name(NAME)
                .name(NAME)
                .email(EMAIL)
                .phone(PHONE)
                .rating(RATING);
    }

    public static DriverRequestDto getDriverRequestDto() {
        return getDriverRequestDtoBuilder().build();
    }

    public static DriverRequestDto getInvalidDriverRequestDto() {
        return getDriverRequestDtoBuilder()
                .name(INVALID_NAME)
                .email(INVALID_EMAIL)
                .phone(INVALID_PHONE)
                .rating(INVALID_RATING)
                .build();
    }

    public static DriverRequestDto getEmptyDriverRequestDto() {
        return getDriverRequestDtoBuilder()
                .name(null)
                .email(null)
                .phone(null)
                .rating(null)
                .build();
    }

    public static DriverResponseDto.DriverResponseDtoBuilder getDriverResponseDtoBuilder() {
        return DriverResponseDto.builder()
                .id(DRIVER_ID)
                .name(NAME)
                .email(EMAIL)
                .phone(PHONE)
                .rating(RATING);
    }

    public static DriverResponseDto getDriverResponseDto() {
        return getDriverResponseDtoBuilder().build();
    }

    public static List<Driver> getDriverList() {
        return List.of(getDriver());
    }

    public static List<DriverResponseDto> getDriverResponseDtoList() {
        return List.of(getDriverResponseDto());
    }

    public static UserRatingDto.UserRatingDtoBuilder getUserRatingDtoBuilder() {
        return UserRatingDto.builder()
                .id(DRIVER_ID)
                .rating(NEW_RATING);
    }

    public static UserRatingDto getUserRatingDto() {
        return getUserRatingDtoBuilder().build();
    }
}