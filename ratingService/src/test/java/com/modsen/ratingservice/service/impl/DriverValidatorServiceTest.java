package com.modsen.ratingservice.service.impl;

import com.modsen.ratingservice.client.ride.RideClientService;
import com.modsen.ratingservice.dto.RatingRequestDto;
import com.modsen.ratingservice.exception.DuplicateFieldException;
import com.modsen.ratingservice.exception.InvalidFieldValueException;
import com.modsen.ratingservice.exception.InvalidStateException;
import com.modsen.ratingservice.repository.DriverRatingRepository;
import com.modsen.ratingservice.util.AppConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.modsen.ratingservice.util.TestData.INVALID_USER_ID;
import static com.modsen.ratingservice.util.TestData.getRatingRequestDto;
import static com.modsen.ratingservice.util.TestData.getRatingRequestDtoBuilder;
import static com.modsen.ratingservice.util.TestData.getRideResponseDto;
import static com.modsen.ratingservice.util.TestData.getRideResponseDtoWithInvalidState;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DriverValidatorServiceTest {

    @Mock
    private DriverRatingRepository driverRatingRepository;

    @Mock
    private RideClientService rideClientService;

    @InjectMocks
    private DriverValidatorService validator;

    @Test
    void validateForCreate_NonExistingRatingAndCorrectUserIdAndCorrectRideState() {
        //Arrange
        when(driverRatingRepository.existsByRideIdAndDeletedIsFalse(anyLong())).thenReturn(false);
        when(rideClientService.getRideById(anyLong())).thenReturn(getRideResponseDto());

        //Act
        //Assert
        assertDoesNotThrow(() -> validator.validateForCreate(getRatingRequestDto()));
    }

    @Test
    void validateForCreate_ExistingRating() {
        //Arrange
        when(driverRatingRepository.existsByRideIdAndDeletedIsFalse(anyLong())).thenReturn(true);

        //Act
        //Assert
        assertThrows(DuplicateFieldException.class,
                () -> validator.validateForCreate(getRatingRequestDto()),
                AppConstants.RATING_FOR_RIDE_ALREADY_EXIST);
    }

    @Test
    void validateForCreate_NonExistingRatingAndNotCorrectUserId() {
        //Arrange
        RatingRequestDto ratingRequestDto = getRatingRequestDtoBuilder()
                .userId(INVALID_USER_ID)
                .build();
        when(driverRatingRepository.existsByRideIdAndDeletedIsFalse(anyLong())).thenReturn(false);
        when(rideClientService.getRideById(anyLong())).thenReturn(getRideResponseDto());

        //Act
        //Assert
        assertThrows(InvalidFieldValueException.class,
                () -> validator.validateForCreate(ratingRequestDto),
                AppConstants.RATING_FOR_RIDE_ALREADY_EXIST);
    }

    @Test
    void validateForCreate_NonExistingRatingAndCorrectUserIdAndNotCorrectRideState() {
        //Arrange
        RatingRequestDto ratingRequestDto = getRatingRequestDto();
        when(driverRatingRepository.existsByRideIdAndDeletedIsFalse(anyLong())).thenReturn(false);
        when(rideClientService.getRideById(anyLong())).thenReturn(getRideResponseDtoWithInvalidState());

        //Act
        //Assert
        assertThrows(InvalidStateException.class,
                () -> validator.validateForCreate(ratingRequestDto),
                AppConstants.INVALID_RIDE_STATE);
    }
}