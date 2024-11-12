package com.modsen.ratingservice.service.impl;

import com.modsen.ratingservice.client.ride.RideClientService;
import com.modsen.ratingservice.dto.RideResponseDto;
import com.modsen.ratingservice.exception.DuplicateFieldException;
import com.modsen.ratingservice.exception.InvalidFieldValueException;
import com.modsen.ratingservice.repository.DriverRatingRepository;
import com.modsen.ratingservice.util.AppConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.modsen.ratingservice.util.TestData.RIDE_ID;
import static com.modsen.ratingservice.util.TestData.getRideResponseDto;
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
    void ratingExistsByRideId_NonExistingRating() {
        //Arrange
        when(driverRatingRepository.existsByRideIdAndDeletedIsFalse(anyLong())).thenReturn(false);

        //Act
        //Assert
        assertDoesNotThrow(() -> validator.ratingExistsByRideId(RIDE_ID));
    }

    @Test
    void ratingExistsByRideId_ExistingRating_ReturnsDuplicateFieldException() {
        //Arrange
        when(driverRatingRepository.existsByRideIdAndDeletedIsFalse(anyLong())).thenReturn(true);

        //Act
        //Assert
        assertThrows(DuplicateFieldException.class,
                () -> validator.ratingExistsByRideId(RIDE_ID),
                AppConstants.RATING_FOR_RIDE_ALREADY_EXIST);
    }

    @Test
    void rideExistsAndUserIsCorrect_CorrectUser() {
        //Arrange
        RideResponseDto rideResponseDto = getRideResponseDto();
        when(rideClientService.getRideById(anyLong())).thenReturn(rideResponseDto);

        //Act
        //Assert
        assertDoesNotThrow(() -> validator.rideExistsAndUserIsCorrect(rideResponseDto.id(), rideResponseDto.driverId()));
    }

    @Test
    void rideExistsAndUserIsCorrect_IncorrectUser_ReturnsInvalidFieldValueException() {
        //Arrange
        RideResponseDto rideResponseDto = getRideResponseDto();
        when(rideClientService.getRideById(anyLong())).thenReturn(rideResponseDto);

        //Act
        //Assert
        assertThrows(InvalidFieldValueException.class,
                () -> validator.rideExistsAndUserIsCorrect(rideResponseDto.id(), rideResponseDto.driverId() + 1),
                AppConstants.DIFFERENT_PASSENGERS_ID);
    }
}