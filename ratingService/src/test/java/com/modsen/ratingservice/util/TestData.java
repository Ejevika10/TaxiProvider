package com.modsen.ratingservice.util;

import com.modsen.ratingservice.dto.RatingRequestDto;
import com.modsen.ratingservice.dto.RatingResponseDto;
import com.modsen.ratingservice.dto.RideResponseDto;
import com.modsen.ratingservice.model.DriverRating;
import com.modsen.ratingservice.model.PassengerRating;
import com.modsen.ratingservice.model.RideState;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestData {
    public static final String RATING_ID = "qwertyuiop1234";
    public static final String NON_EXISTING_RATING_ID = "qwertyuiop1234";
    public static final Long USER_ID = 1L;
    public static final Long INSUFFICIENT_USER_ID = -1L;
    public static final Long RIDE_ID = 1L;
    public static final Long INSUFFICIENT_RIDE_ID = -1L;

    public static final String URL_DRIVER_RATING = "/api/v1/driverratings";
    public static final String URL_DRIVER_RATING_ID = URL_DRIVER_RATING + "/{ratingId}";
    public static final String URL_DRIVER_RATING_USER_ID = URL_DRIVER_RATING + "/user/{userId}";
    public static final String URL_PASSENGER_RATING = "/api/v1/passengerratings";
    public static final String URL_PASSENGER_RATING_ID = URL_PASSENGER_RATING + "/{ratingId}";
    public static final String URL_PASSENGER_RATING_USER_ID = URL_PASSENGER_RATING + "/user/{userId}";

    public static final String OFFSET = "offset";
    public static final String LIMIT = "limit";
    public static final Integer OFFSET_VALUE = 0;
    public static final Integer LIMIT_VALUE = 5;
    public static final Integer INSUFFICIENT_OFFSET_VALUE = -1;
    public static final Integer INSUFFICIENT_LIMIT_VALUE = -1;
    public static final Integer EXCEEDED_OFFSET_VALUE = 100;
    public static final Integer EXCEEDED_LIMIT_VALUE = 100;

    private static final Integer RATING = 5;
    public static final Integer NEW_RATING = 3;
    private static final Integer INVALID_RATING = 7;
    private static final String COMMENT = "This is a comment";
    public static final String NEW_COMMENT = "This is a new comment";


    public static DriverRating.DriverRatingBuilder getDriverRatingBuilder() {
        return DriverRating.builder()
                .id(RATING_ID)
                .userId(USER_ID)
                .rideId(RIDE_ID)
                .comment(COMMENT)
                .rating(RATING);
    }

    public static DriverRating getDriverRating() {
        return getDriverRatingBuilder().build();
    }

    public static PassengerRating.PassengerRatingBuilder getPassengerRatingBuilder() {
        return PassengerRating.builder()
                .id(RATING_ID)
                .userId(USER_ID)
                .rideId(RIDE_ID)
                .comment(COMMENT)
                .rating(RATING);
    }

    public static PassengerRating getPassengerRating() {
        return getPassengerRatingBuilder().build();
    }

    public static RatingRequestDto.RatingRequestDtoBuilder getRatingRequestDtoBuilder() {
        return RatingRequestDto.builder()
                .userId(USER_ID)
                .rideId(RIDE_ID)
                .rating(RATING)
                .comment(COMMENT);
    }

    public static RatingRequestDto getRatingRequestDto() {
        return getRatingRequestDtoBuilder().build();
    }

    public static RatingRequestDto getEmptyRatingRequestDto() {
        return getRatingRequestDtoBuilder()
                .userId(null)
                .rideId(null)
                .rating(null)
                .comment(null)
                .build();
    }

    public static RatingRequestDto getInvalidRatingRequestDto() {
        return getRatingRequestDtoBuilder()
                .userId(INSUFFICIENT_USER_ID)
                .rideId(INSUFFICIENT_RIDE_ID)
                .rating(INVALID_RATING)
                .comment(null)
                .build();
    }

    public static RatingResponseDto.RatingResponseDtoBuilder getRatingResponseDtoBuilder() {
        return RatingResponseDto.builder()
                .id(RATING_ID)
                .userId(USER_ID)
                .rideId(RIDE_ID)
                .rating(RATING)
                .comment(COMMENT);
    }

    public static RatingResponseDto getRatingResponseDto() {
        return getRatingResponseDtoBuilder().build();
    }

    public static List<DriverRating> getDriverRatingList() {
        return List.of(getDriverRating());
    }

    public static List<PassengerRating> getPassengerRatingList() {
        return List.of(getPassengerRating());
    }

    public static List<RatingResponseDto> getRatingResponseDtoList() {
        return List.of(getRatingResponseDto());
    }

    public static RideResponseDto getRideResponseDto() {
        return new RideResponseDto(RIDE_ID, USER_ID, USER_ID, "Sourse address", "Destination address", RideState.CREATED, LocalDateTime.now(), 1000);
    }
}