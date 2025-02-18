package com.modsen.passengerservice.util;

import com.modsen.passengerservice.dto.PageDto;
import com.modsen.passengerservice.dto.PassengerCreateRequestDto;
import com.modsen.passengerservice.dto.PassengerResponseDto;
import com.modsen.passengerservice.dto.PassengerUpdateRequestDto;
import com.modsen.passengerservice.dto.UserRatingDto;
import com.modsen.passengerservice.e2e.dto.LoginRequestDto;
import com.modsen.passengerservice.model.Passenger;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestData {
    public static final UUID PASSENGER_ID = new UUID(1,1);
    public static final UUID PASSENGER_ID_2 = new UUID(1,2);
    public static final String INVALID_PASSENGER_ID = "invalid-uuid-string";
    public static final String URL_PASSENGER = "/api/v1/passengers";
    public static final String URL_PASSENGER_ID = URL_PASSENGER + "/{passengerId}";
    public static final String OFFSET = "offset";
    public static final String LIMIT = "limit";
    public static final String PAGE_SIZE = "pageSize";
    public static final String PAGE_NUMBER = "pageNumber";
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final Integer OFFSET_VALUE = 0;
    public static final Integer LIMIT_VALUE = 5;
    public static final Integer INSUFFICIENT_OFFSET_VALUE = -1;
    public static final Integer INSUFFICIENT_LIMIT_VALUE = -1;
    public static final Integer EXCEEDED_OFFSET_VALUE = 100;
    public static final Integer EXCEEDED_LIMIT_VALUE = 100;
    private static final String NAME = "passenger";
    private static final String EMAIL = "passenger@mail.ru";
    public static final String UNIQUE_EMAIL = "passenger_unique@mail.ru";
    private static final String PHONE = "71234567890";
    private static final Double RATING = 0.0;
    public static final Double NEW_RATING = 5.0;

    private static final String INVALID_NAME = "p";
    private static final String INVALID_EMAIL = "passenger";
    private static final String INVALID_PHONE = "11";
    private static final Double INVALID_RATING = -1.0;

    public static final String URL_AUTHENTICATION = "http://localhost:8085/api/v1/auth/login";
    public static final String USERNAME = "admin";
    public static final String PASSWORD = "admin";

    public static final String PASSENGER_SCRIPT = "passenger-controller-preparation.sql";

    public static Passenger.PassengerBuilder getPassengerBuilder() {
        return Passenger.builder()
                .id(PASSENGER_ID)
                .name(NAME)
                .email(EMAIL)
                .phone(PHONE)
                .rating(RATING)
                .deleted(false);
    }

    public static Passenger getPassenger() {
        return getPassengerBuilder().build();
    }

    public static PassengerCreateRequestDto.PassengerCreateRequestDtoBuilder getPassengerCreateRequestDtoBuilder() {
        return PassengerCreateRequestDto.builder()
                .id(String.valueOf(PASSENGER_ID))
                .name(NAME)
                .email(EMAIL)
                .phone(PHONE)
                .rating(RATING);
    }

    public static PassengerCreateRequestDto getPassengerCreateRequestDto() {
        return getPassengerCreateRequestDtoBuilder().build();
    }

    public static PassengerCreateRequestDto getEmptyPassengerCreateRequestDto() {
        return getPassengerCreateRequestDtoBuilder()
                .id(null)
                .name(null)
                .email(null)
                .phone(null)
                .rating(null)
                .build();
    }

    public static PassengerCreateRequestDto getInvalidPassengerCreateRequestDto() {
        return getPassengerCreateRequestDtoBuilder()
                .id(INVALID_PASSENGER_ID)
                .name(INVALID_NAME)
                .email(INVALID_EMAIL)
                .phone(INVALID_PHONE)
                .rating(INVALID_RATING)
                .build();
    }

    public static PassengerUpdateRequestDto.PassengerUpdateRequestDtoBuilder getPassengerUpdateRequestDtoBuilder() {
        return PassengerUpdateRequestDto.builder()
                .name(NAME)
                .email(EMAIL)
                .phone(PHONE);
    }

    public static PassengerUpdateRequestDto getPassengerUpdateRequestDto() {
        return getPassengerUpdateRequestDtoBuilder().build();
    }

    public static PassengerUpdateRequestDto getEmptyPassengerUpdateRequestDto() {
        return getPassengerUpdateRequestDtoBuilder()
                .name(null)
                .email(null)
                .phone(null)
                .build();
    }

    public static PassengerUpdateRequestDto getInvalidPassengerUpdateRequestDto() {
        return getPassengerUpdateRequestDtoBuilder()
                .name(INVALID_NAME)
                .email(INVALID_EMAIL)
                .phone(INVALID_PHONE)
                .build();
    }

    public static PassengerResponseDto.PassengerResponseDtoBuilder getPassengerResponseDtoBuilder() {
        return PassengerResponseDto.builder()
                .id(PASSENGER_ID)
                .name(NAME)
                .email(EMAIL)
                .phone(PHONE)
                .rating(RATING);
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

    public static PageDto<PassengerResponseDto> getPagePassengerResponseDto() {
        return new PageDto<>(
                OFFSET_VALUE,
                LIMIT_VALUE,
                1,
                1,
                getPassengerResponseDtoList()
        );
    }

    public static UserRatingDto.UserRatingDtoBuilder getUserRatingDtoBuilder() {
        return UserRatingDto.builder()
                .id(PASSENGER_ID)
                .rating(NEW_RATING);
    }

    public static UserRatingDto getUserRatingDto() {
        return getUserRatingDtoBuilder().build();
    }

    public static LoginRequestDto.LoginRequestDtoBuilder getLoginRequestDtoBuilder() {
        return LoginRequestDto.builder()
                .username(USERNAME)
                .password(PASSWORD);
    }

    public static LoginRequestDto getLoginRequestDto() {
        return getLoginRequestDtoBuilder().build();
    }
}