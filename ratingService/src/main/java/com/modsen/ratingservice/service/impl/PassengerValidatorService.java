package com.modsen.ratingservice.service.impl;

import com.modsen.ratingservice.client.ride.RideClientService;
import com.modsen.ratingservice.dto.RideResponseDto;
import com.modsen.ratingservice.exception.DuplicateFieldException;
import com.modsen.ratingservice.exception.InvalidFieldValueException;
import com.modsen.ratingservice.repository.PassengerRatingRepository;
import com.modsen.ratingservice.util.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PassengerValidatorService {

    private final PassengerRatingRepository passengerRatingRepository;
    private final MessageSource messageSource;
    private final RideClientService rideClientService;

    public void ratingExistsByRideId(long rideId) {
        if (passengerRatingRepository.existsByRideIdAndDeletedIsFalse(rideId)) {
            throw new DuplicateFieldException(
                    messageSource.getMessage(AppConstants.RATING_FOR_RIDE_ALREADY_EXIST,
                            new Object[]{}, LocaleContextHolder.getLocale()));
        }
    }

    public void rideExistsAndUserIsCorrect(long rideId, long userId){
        RideResponseDto rideResponseDto = rideClientService.getRideById(rideId);
        if(rideResponseDto.passengerId() != userId) {
            throw new InvalidFieldValueException(
                    messageSource.getMessage(AppConstants.DIFFERENT_PASSENGERS_ID,
                            new Object[]{}, LocaleContextHolder.getLocale()));
        }
    }
}
