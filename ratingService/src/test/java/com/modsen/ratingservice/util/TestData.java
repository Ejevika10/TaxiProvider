package com.modsen.ratingservice.util;

import com.modsen.ratingservice.dto.PageDto;
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
    public static final Long INVALID_USER_ID = 2L;
    public static final Long INSUFFICIENT_USER_ID = -1L;
    public static final Long RIDE_ID = 1L;
    public static final Long UNIQUE_RIDE_ID = 2L;
    public static final Long INSUFFICIENT_RIDE_ID = -1L;
    public static final String RIDE_SERVICE_NAME = "ride-service";
    public static final int RIDE_SERVICE_PORT = 8888;

    public static final String URL_DRIVER_RATING = "/api/v1/driverratings";
    public static final String URL_DRIVER_RATING_ID = URL_DRIVER_RATING + "/{ratingId}";
    public static final String URL_DRIVER_RATING_USER_ID = URL_DRIVER_RATING + "/user/{userId}";
    public static final String URL_PASSENGER_RATING = "/api/v1/passengerratings";
    public static final String URL_PASSENGER_RATING_ID = URL_PASSENGER_RATING + "/{ratingId}";
    public static final String URL_PASSENGER_RATING_USER_ID = URL_PASSENGER_RATING + "/user/{userId}";
    public static final String URL_RIDES =  "/api/v1/rides/";
    public static final String RIDE_NOT_FOUND = "ride not found";
    public static final String PASSENGER_ID_INVALID = "There is another passengerId in the ride";
    public static final String DRIVER_ID_INVALID = "There is another driverId in the ride";

    public static final String PAGE_SIZE = "pageSize";
    public static final String PAGE_NUMBER = "pageNumber";
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

    public static final Integer FIRST_RATING = 1;
    public static final Integer SECOND_RATING = 2;
    public static final Integer THIRD_RATING = 3;
    public static final double AVG_RATING = (double)(FIRST_RATING + SECOND_RATING + THIRD_RATING) / 3;
    public static final double MIN_RATING = 0.0;

    public static DriverRating.DriverRatingBuilder getDriverRatingBuilder() {
        return DriverRating.builder()
                .id(RATING_ID)
                .userId(USER_ID)
                .rideId(RIDE_ID)
                .comment(COMMENT)
                .rating(RATING)
                .deleted(false);
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
                .rating(RATING)
                .deleted(false);
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

    public static PageDto<RatingResponseDto> getPageRatingResponseDto() {
        return new PageDto<>(
                OFFSET_VALUE,
                LIMIT_VALUE,
                1,
                1,
                getRatingResponseDtoList()
        );
    }

    public static List<RatingResponseDto> getRatingResponseDtoListWithDifferentRatings() {
        return List.of(
                getRatingResponseDtoBuilder()
                        .rating(FIRST_RATING)
                        .build(),
                getRatingResponseDtoBuilder()
                        .rating(SECOND_RATING)
                        .build(),
                getRatingResponseDtoBuilder()
                        .rating(THIRD_RATING)
                        .build());
    }

    public static List<RatingResponseDto> getRatingResponseDtoListEmpty() {
        return List.of();
    }

    public static RideResponseDto getRideResponseDto() {
        return new RideResponseDto(RIDE_ID, USER_ID, USER_ID, "Sourse address", "Destination address", RideState.COMPLETED, LocalDateTime.now(), 1000);
    }

    public static RideResponseDto getRideResponseDtoWithInvalidState() {
        return new RideResponseDto(RIDE_ID, USER_ID, USER_ID, "Sourse address", "Destination address", RideState.ACCEPTED, LocalDateTime.now(), 1000);
    }
}