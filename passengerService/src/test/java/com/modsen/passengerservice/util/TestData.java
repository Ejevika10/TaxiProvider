package com.modsen.passengerservice.util;

import com.modsen.passengerservice.dto.PassengerRequestDto;
import com.modsen.passengerservice.dto.PassengerResponseDto;
import com.modsen.passengerservice.dto.UserRatingDto;
import com.modsen.passengerservice.model.Passenger;

import java.util.List;

public class TestData {
    public static final Long PASSENGER_ID = 1L;
    public static final Long INSUFFICIENT_PASSENGER_ID = -1L;
    public static final String URL_PASSENGER = "/api/v1/passengers";
    public static final String URL_PASSENGER_ID = URL_PASSENGER + "/{passengerId}";
    public static final String OFFSET = "offset";
    public static final String LIMIT = "limit";

    public static final Integer OFFSET_VALUE = 0;
    public static final Integer LIMIT_VALUE = 5;
    public static final Integer INSUFFICIENT_OFFSET_VALUE = -1;
    public static final Integer INSUFFICIENT_LIMIT_VALUE = -1;
    public static final Integer EXCEEDED_OFFSET_VALUE = 100;
    public static final Integer EXCEEDED_LIMIT_VALUE = 100;
    private static final String name = "passenger";
    private static final String email = "passenger@mail.ru";
    private static final String phone = "71234567890";
    private static final Double rating = 0.0;
    public static final Double NEW_RATING = 5.0;

    private static final String invalidName = "p";
    private static final String invalidEmail = "passenger";
    private static final String invalidPhone = "11";
    private static final Double invalidRating = -1.0;

    public static Passenger.PassengerBuilder getPassengerBuilder() {
        return Passenger.builder()
                .id(PASSENGER_ID)
                .name(name)
                .email(email)
                .phone(phone)
                .rating(rating)
                .deleted(false);
    }

    public static Passenger getPassenger() {
        return getPassengerBuilder().build();
    }

    public static PassengerRequestDto.PassengerRequestDtoBuilder getPassengerRequestDtoBuilder() {
        return PassengerRequestDto.builder()
                .name(name)
                .email(email)
                .phone(phone)
                .rating(rating);
    }

    public static PassengerRequestDto getPassengerRequestDto() {
        return getPassengerRequestDtoBuilder().build();
    }

    public static PassengerRequestDto getEmptyPassengerRequestDto() {
        return getPassengerRequestDtoBuilder()
                .name(null)
                .email(null)
                .phone(null)
                .rating(null)
                .build();
    }

    public static PassengerRequestDto getInvalidPassengerRequestDto() {
        return getPassengerRequestDtoBuilder()
                .name(invalidName)
                .email(invalidEmail)
                .phone(invalidPhone)
                .rating(invalidRating)
                .build();
    }

    public static PassengerResponseDto.PassengerResponseDtoBuilder getPassengerResponseDtoBuilder() {
        return PassengerResponseDto.builder()
                .id(PASSENGER_ID)
                .name(name)
                .email(email)
                .phone(phone)
                .rating(rating);
    }

    public static PassengerResponseDto getPassengerResponseDto() {
        return getPassengerResponseDtoBuilder().build();
    }

    public static List<Passenger> getPassengerList() {
        return List.of(getPassenger());
    }

    public static List<PassengerResponseDto> getPassengerResponseDtoList() {
        return List.of(getPassengerResponseDto());
    }

    public static UserRatingDto.UserRatingDtoBuilder getUserRatingDtoBuilder() {
        return UserRatingDto.builder()
                .id(PASSENGER_ID)
                .rating(NEW_RATING);
    }

    public static UserRatingDto getUserRatingDto() {
        return getUserRatingDtoBuilder().build();
    }
}
