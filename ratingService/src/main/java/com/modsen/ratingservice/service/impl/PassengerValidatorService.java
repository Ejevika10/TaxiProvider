package com.modsen.ratingservice.service.impl;

import com.modsen.exceptionstarter.exception.DuplicateFieldException;
import com.modsen.exceptionstarter.exception.InvalidFieldValueException;
import com.modsen.exceptionstarter.exception.InvalidStateException;
import com.modsen.ratingservice.client.ride.RideClientService;
import com.modsen.ratingservice.dto.RatingRequestDto;
import com.modsen.ratingservice.dto.RideResponseDto;
import com.modsen.ratingservice.model.RideState;
import com.modsen.ratingservice.repository.PassengerRatingRepository;
import com.modsen.ratingservice.util.MessageConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PassengerValidatorService {

    private final PassengerRatingRepository passengerRatingRepository;
    private final RideClientService rideClientService;

    public void validateForCreate(RatingRequestDto ratingRequestDto, String bearerToken){
        Long rideId = ratingRequestDto.rideId();
        ratingDoesntExistsByRideId(rideId);
        RideResponseDto ride = rideExistsByRideId(rideId, bearerToken);
        userIdIsCorrect(ride, UUID.fromString(ratingRequestDto.userId()));
        rideStateIsCorrect(ride.rideState());
    }

    public void validateForUpdate(RatingRequestDto ratingRequestDto, String bearerToken){
        RideResponseDto ride = rideExistsByRideId(ratingRequestDto.rideId(), bearerToken);
        userIdIsCorrect(ride, UUID.fromString(ratingRequestDto.userId()));
        rideStateIsCorrect(ride.rideState());
    }

    public void ratingDoesntExistsByRideId(long rideId) {
        if (passengerRatingRepository.existsByRideIdAndDeletedIsFalse(rideId)) {
            throw new DuplicateFieldException(MessageConstants.RATING_FOR_RIDE_ALREADY_EXIST);
        }
    }

    private RideResponseDto rideExistsByRideId(long rideId, String bearerToken) {
        return rideClientService.getRideById(rideId, bearerToken);
    }

    private void userIdIsCorrect(RideResponseDto rideResponseDto, UUID userId) {
        if (!rideResponseDto.passengerId().equals(userId)) {
            throw new InvalidFieldValueException(MessageConstants.DIFFERENT_PASSENGERS_ID);
        }
    }

    private void rideStateIsCorrect(RideState rideState) {
        if (rideState != RideState.COMPLETED && rideState != RideState.CANCELLED) {
            throw new InvalidStateException(MessageConstants.INVALID_RIDE_STATE);
        }
    }
}
