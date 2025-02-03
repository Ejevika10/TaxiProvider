package com.modsen.ratingservice.service.impl;

import com.modsen.ratingservice.client.ride.RideClientService;
import com.modsen.ratingservice.dto.RatingRequestDto;
import com.modsen.ratingservice.dto.RideResponseDto;
import com.modsen.ratingservice.exception.DuplicateFieldException;
import com.modsen.ratingservice.exception.InvalidFieldValueException;
import com.modsen.ratingservice.exception.InvalidStateException;
import com.modsen.ratingservice.model.RideState;
import com.modsen.ratingservice.repository.PassengerRatingRepository;
import com.modsen.ratingservice.util.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PassengerValidatorService {

    private final PassengerRatingRepository passengerRatingRepository;
    private final RideClientService rideClientService;

    public void validateForCreate(RatingRequestDto ratingRequestDto, String authorizationToken){
        Long rideId = ratingRequestDto.rideId();
        ratingDoesntExistsByRideId(rideId);
        RideResponseDto ride = rideExistsByRideId(rideId, authorizationToken);
        userIdIsCorrect(ride, UUID.fromString(ratingRequestDto.userId()));
        rideStateIsCorrect(ride.rideState());
    }

    public void validateForUpdate(RatingRequestDto ratingRequestDto, String authorizationToken){
        RideResponseDto ride = rideExistsByRideId(ratingRequestDto.rideId(), authorizationToken);
        userIdIsCorrect(ride, UUID.fromString(ratingRequestDto.userId()));
        rideStateIsCorrect(ride.rideState());
    }

    public void ratingDoesntExistsByRideId(long rideId) {
        if (passengerRatingRepository.existsByRideIdAndDeletedIsFalse(rideId)) {
            throw new DuplicateFieldException(AppConstants.RATING_FOR_RIDE_ALREADY_EXIST);
        }
    }

    private RideResponseDto rideExistsByRideId(long rideId, String authorizationToken) {
        return rideClientService.getRideById(rideId, authorizationToken);
    }

    private void userIdIsCorrect(RideResponseDto rideResponseDto, UUID userId) {
        if (!rideResponseDto.passengerId().equals(userId)) {
            throw new InvalidFieldValueException(AppConstants.DIFFERENT_PASSENGERS_ID);
        }
    }

    private void rideStateIsCorrect(RideState rideState) {
        if (rideState != RideState.COMPLETED && rideState != RideState.CANCELLED) {
            throw new InvalidStateException(AppConstants.INVALID_RIDE_STATE);
        }
    }
}
