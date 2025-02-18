package com.modsen.ratingservice.service.impl;

import com.modsen.exceptionstarter.exception.DuplicateFieldException;
import com.modsen.ratingservice.client.ride.RideClientService;
import com.modsen.ratingservice.repository.PassengerRatingRepository;
import com.modsen.ratingservice.util.AppConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.modsen.ratingservice.util.TestData.RIDE_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PassengerValidatorServiceTest {

    @Mock
    private PassengerRatingRepository passengerRatingRepository;

    @Mock
    private RideClientService rideClientService;

    @InjectMocks
    private PassengerValidatorService validator;

    @Test
    void ratingExistsByRideId_NonExistingRating() {
        //Arrange
        when(passengerRatingRepository.existsByRideIdAndDeletedIsFalse(anyLong())).thenReturn(false);

        //Act
        //Assert
        assertDoesNotThrow(() -> validator.ratingDoesntExistsByRideId(RIDE_ID));
    }

    @Test
    void ratingExistsByRideId_ExistingRating_ReturnsDuplicateFieldException() {
        //Arrange
        when(passengerRatingRepository.existsByRideIdAndDeletedIsFalse(anyLong())).thenReturn(true);

        //Act
        //Assert
        assertThrows(DuplicateFieldException.class,
                () -> validator.ratingDoesntExistsByRideId(RIDE_ID),
                AppConstants.RATING_FOR_RIDE_ALREADY_EXIST);
    }
}