package com.modsen.ratingservice.service.impl;

import com.modsen.exceptionstarter.exception.DuplicateFieldException;
import com.modsen.exceptionstarter.exception.InvalidFieldValueException;
import com.modsen.exceptionstarter.exception.InvalidStateException;
import com.modsen.ratingservice.client.ride.RideClientService;
import com.modsen.ratingservice.dto.RatingRequestDto;
import com.modsen.ratingservice.dto.RideResponseDto;
import com.modsen.ratingservice.model.RideState;
import com.modsen.ratingservice.repository.DriverRatingRepository;
import com.modsen.ratingservice.util.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DriverValidatorService {

    private final DriverRatingRepository driverRatingRepository;
    private final RideClientService rideClientService;

    public void validateForCreate(RatingRequestDto ratingRequestDto, String authorizationToken){
        Long rideId = ratingRequestDto.rideId();
        ratingDoesntExistsByRideId(rideId);
        RideResponseDto ride = rideExistsById(rideId, authorizationToken);
        userIdIsCorrect(ride, UUID.fromString(ratingRequestDto.userId()));
        rideStateIsCorrect(ride.rideState());
    }

    public void validateForUpdate(RatingRequestDto ratingRequestDto, String authorizationToken){
        RideResponseDto ride = rideExistsById(ratingRequestDto.rideId(), authorizationToken);
        userIdIsCorrect(ride, UUID.fromString(ratingRequestDto.userId()));
        rideStateIsCorrect(ride.rideState());
    }

    public void ratingDoesntExistsByRideId(long rideId) {
        if (driverRatingRepository.existsByRideIdAndDeletedIsFalse(rideId)) {
            throw new DuplicateFieldException(AppConstants.RATING_FOR_RIDE_ALREADY_EXIST);
        }
    }

    private RideResponseDto rideExistsById(long rideId, String authorizationToken) {
        return rideClientService.getRideById(rideId, authorizationToken);
    }

    private void userIdIsCorrect(RideResponseDto rideResponseDto, UUID userId) {
        if (!rideResponseDto.driverId().equals(userId)) {
            throw new InvalidFieldValueException(AppConstants.DIFFERENT_DRIVERS_ID);
        }
    }

    private void rideStateIsCorrect(RideState rideState) {
        if (rideState != RideState.COMPLETED && rideState != RideState.CANCELLED) {
            throw new InvalidStateException(AppConstants.INVALID_RIDE_STATE);
        }
    }
}
