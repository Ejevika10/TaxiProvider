package com.modsen.ratingservice.service.impl;

import com.modsen.ratingservice.client.ride.RideClientService;
import com.modsen.ratingservice.dto.RideResponseDto;
import com.modsen.ratingservice.exception.DuplicateFieldException;
import com.modsen.ratingservice.exception.InvalidFieldValueException;
import com.modsen.ratingservice.exception.InvalidStateException;
import com.modsen.ratingservice.model.RideState;
import com.modsen.ratingservice.repository.DriverRatingRepository;
import com.modsen.ratingservice.util.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverValidatorService {

    private final DriverRatingRepository driverRatingRepository;
    private final RideClientService rideClientService;

    public void ratingExistsByRideId(long rideId) {
        if (driverRatingRepository.existsByRideIdAndDeletedIsFalse(rideId)) {
            throw new DuplicateFieldException(AppConstants.RATING_FOR_RIDE_ALREADY_EXIST);
        }
    }

    public void rideExistsAndUserIsCorrectAndRideStateIsCorrect(long rideId, long userId) {
        RideResponseDto rideResponseDto = rideClientService.getRideById(rideId);
        if (rideResponseDto.driverId().compareTo(userId) != 0) {
            throw new InvalidFieldValueException(AppConstants.DIFFERENT_DRIVERS_ID);
        }
        if (rideResponseDto.rideState() != RideState.COMPLETED && rideResponseDto.rideState() != RideState.CANCELLED) {
            throw new InvalidStateException(AppConstants.INVALID_RIDE_STATE);
        }
    }
}
