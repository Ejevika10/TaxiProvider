package com.modsen.ratingservice.service.impl;

import com.modsen.ratingservice.client.ride.RideClientService;
import com.modsen.ratingservice.dto.RideResponseDto;
import com.modsen.ratingservice.exception.DuplicateFieldException;
import com.modsen.ratingservice.exception.InvalidFieldValueException;
import com.modsen.ratingservice.repository.DriverRatingRepository;
import com.modsen.ratingservice.util.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverValidatorService {

    private final DriverRatingRepository driverRatingRepository;
    private final MessageSource messageSource;
    private final RideClientService rideClientService;

    public void ratingExistsByRideId(long rideId){
        if (driverRatingRepository.existsByRideIdAndDeletedIsFalse(rideId)) {
            throw new DuplicateFieldException(
                    messageSource.getMessage(AppConstants.RATING_FOR_RIDE_ALREADY_EXIST,
                            new Object[]{}, LocaleContextHolder.getLocale()));
        }
    }

    public void rideExistsAndUserIsCorrect(long rideId, long userId){
        RideResponseDto rideResponseDto = rideClientService.getRideById(rideId);
        if(rideResponseDto.driverId().compareTo(userId) != 0) {
            throw new InvalidFieldValueException(
                    messageSource.getMessage(AppConstants.DIFFERENT_DRIVERS_ID,
                            new Object[]{}, LocaleContextHolder.getLocale()));
        }
    }
}
