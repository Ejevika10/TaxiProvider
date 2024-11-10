package com.modsen.driverservice.util;

import com.modsen.driverservice.dto.CarRequestDto;
import com.modsen.driverservice.dto.CarResponseDto;
import com.modsen.driverservice.dto.DriverRequestDto;
import com.modsen.driverservice.dto.DriverResponseDto;
import com.modsen.driverservice.dto.UserRatingDto;
import com.modsen.driverservice.model.Car;
import com.modsen.driverservice.model.Driver;

import java.util.List;

public class TestData {

    public static final String URL_CAR = "/api/v1/cars";
    public static final String URL_CAR_ID = URL_CAR + "/{carId}";
    public static final String URL_CAR_DRIVER_ID = URL_CAR + "/driver/{driverId}";

    public static final String URL_DRIVER = "/api/v1/drivers";
    public static final String URL_DRIVER_ID = URL_DRIVER + "/{driverId}";
    public static final String OFFSET = "offset";
    public static final String LIMIT = "limit";

    public static final Integer OFFSET_VALUE = 0;
    public static final Integer LIMIT_VALUE = 5;
    public static final Integer INSUFFICIENT_OFFSET_VALUE = -1;
    public static final Integer INSUFFICIENT_LIMIT_VALUE = -1;
    public static final Integer EXCEEDED_OFFSET_VALUE = 100;
    public static final Integer EXCEEDED_LIMIT_VALUE = 100;


    public static final Long CAR_ID = 1L;
    private static final String color = "red";
    private static final String model = "sedan";
    private static final String brand = "audi";
    private static final String number = "12345";
    public static final Long DRIVER_ID = 1L;

    public static final Long INSUFFICIENT_CAR_ID = -1L;
    private static final String invalidColor = "r";
    private static final String invalidModel = "s";
    private static final String invalidBrand = "a";
    private static final String invalidNumber = "1";
    public static final Long INSUFFICIENT_DRIVER_ID = -1L;

    private static final String name = "Driver";
    private static final String email = "driver@email.com";
    private static final String phone = "71234567890";
    private static final Double rating = 0.0;

    private static final String invalidName = "d";
    private static final String invalidEmail = "driver";
    private static final String invalidPhone = "11";
    private static final Double invalidRating = -1.0;

    public static Car.CarBuilder getCarBuilder() {
        return Car.builder()
                .id(CAR_ID)
                .color(color)
                .model(model)
                .brand(brand)
                .number(number)
                .deleted(false);
    }

    public static Car getCar() {
        return getCarBuilder().build();
    }

    public static CarRequestDto.CarRequestDtoBuilder getCarRequestDtoBuilder() {
        return CarRequestDto.builder()
                .brand(brand)
                .color(color)
                .model(model)
                .number(number)
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
                .brand(invalidBrand)
                .color(invalidColor)
                .model(invalidModel)
                .number(invalidNumber)
                .driverId(INSUFFICIENT_DRIVER_ID)
                .build();
    }

    public static CarResponseDto.CarResponseDtoBuilder getCarResponseDtoBuilder() {
        return CarResponseDto.builder()
                .id(CAR_ID)
                .brand(brand)
                .color(color)
                .model(model)
                .number(number);
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

    public static Driver.DriverBuilder getDriverBuilder() {
        return Driver.builder()
                .id(DRIVER_ID)
                .name(name)
                .email(email)
                .phone(phone)
                .rating(rating)
                .cars(null)
                .deleted(false);
    }

    public static Driver getDriver() {
        return getDriverBuilder().build();
    }

    public static DriverRequestDto.DriverRequestDtoBuilder getDriverRequestDtoBuilder() {
        return DriverRequestDto.builder()
                .name(name)
                .name(name)
                .email(email)
                .phone(phone)
                .rating(rating);
    }

    public static DriverRequestDto getDriverRequestDto() {
        return getDriverRequestDtoBuilder().build();
    }

    public static DriverRequestDto getInvalidDriverRequestDto() {
        return getDriverRequestDtoBuilder()
                .name(invalidName)
                .email(invalidEmail)
                .phone(invalidPhone)
                .rating(invalidRating)
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
                .name(name)
                .email(email)
                .phone(phone)
                .rating(rating);
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
                .rating(rating);
    }

    public static UserRatingDto getUserRatingDto() {
        return getUserRatingDtoBuilder().build();
    }
}
