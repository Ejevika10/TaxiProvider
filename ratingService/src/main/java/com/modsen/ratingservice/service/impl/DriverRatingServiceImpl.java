package com.modsen.ratingservice.service.impl;

import com.modsen.ratingservice.client.RideClientService;
import com.modsen.ratingservice.dto.PageDto;
import com.modsen.ratingservice.dto.RatingRequestDto;
import com.modsen.ratingservice.dto.RatingResponseDto;
import com.modsen.ratingservice.dto.RideResponseDto;
import com.modsen.ratingservice.exception.DuplicateFieldException;
import com.modsen.ratingservice.exception.InvalidFieldValueException;
import com.modsen.ratingservice.exception.NotFoundException;
import com.modsen.ratingservice.mapper.PageMapper;
import com.modsen.ratingservice.mapper.RatingListMapper;
import com.modsen.ratingservice.mapper.RatingMapper;
import com.modsen.ratingservice.model.DriverRating;
import com.modsen.ratingservice.repository.DriverRatingRepository;
import com.modsen.ratingservice.service.RatingService;
import com.modsen.ratingservice.util.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Qualifier("DriverRatingServiceImpl")
public class DriverRatingServiceImpl implements RatingService {

    private final DriverRatingRepository driverRatingRepository;
    private final RatingMapper ratingMapper;
    private final RatingListMapper ratingListMapper;
    private final MessageSource messageSource;
    private final PageMapper pageMapper;
    private final RideClientService rideClientService;

    @Override
    public List<RatingResponseDto> getAllRatings() {
        List<DriverRating> ratings = driverRatingRepository.findAllByDeletedIsFalse();
        return ratingListMapper.toDriverRatingResponseDtoList(ratings);
    }

    @Override
    public PageDto<RatingResponseDto> getPageRatings(Integer offset, Integer limit) {
        Page<RatingResponseDto> pageRating = driverRatingRepository.findAllByDeletedIsFalse(PageRequest.of(offset, limit))
                .map(ratingMapper::toRatingResponseDto);
        return pageMapper.pageToDto(pageRating);
    }

    @Override
    public List<RatingResponseDto> getAllRatingsByUserId(Long userId) {
        List<DriverRating> ratings = driverRatingRepository.findAllByUserIdAndDeletedIsFalse(userId);
        return ratingListMapper.toDriverRatingResponseDtoList(ratings);
    }

    @Override
    public PageDto<RatingResponseDto> getPageRatingsByUserId(Long userId, Integer offset, Integer limit) {
        Page<RatingResponseDto> pageRating = driverRatingRepository
                .findAllByUserIdAndDeletedIsFalse(userId, PageRequest.of(offset, limit))
                .map(ratingMapper::toRatingResponseDto);
        return pageMapper.pageToDto(pageRating);
    }

    @Override
    public RatingResponseDto getRatingById(String id) {
        DriverRating rating = findByIdOrThrow(id);
        return ratingMapper.toRatingResponseDto(rating);
    }

    @Override
    public RatingResponseDto addRating(RatingRequestDto ratingRequestDto) {
        if (driverRatingRepository.existsByRideIdAndDeletedIsFalse(ratingRequestDto.rideId())) {
            throw new DuplicateFieldException(
                    messageSource.getMessage(AppConstants.RATING_FOR_RIDE_ALREADY_EXIST,
                            new Object[]{}, LocaleContextHolder.getLocale()));
        }
        RideResponseDto rideResponseDto = rideClientService.getRideById(ratingRequestDto.rideId());
        if(rideResponseDto.driverId().compareTo(ratingRequestDto.userId()) != 0) {
            throw new InvalidFieldValueException(
                    messageSource.getMessage(AppConstants.DIFFERENT_DRIVERS_ID,
                            new Object[]{}, LocaleContextHolder.getLocale()));
        }
        DriverRating ratingToSave = ratingMapper.toDriverRating(ratingRequestDto);
        ratingToSave.setDeleted(false);
        DriverRating rating = driverRatingRepository.save(ratingToSave);
        return ratingMapper.toRatingResponseDto(rating);
    }

    @Override
    public RatingResponseDto updateRating(String id, RatingRequestDto ratingRequestDto) {
        DriverRating ratingToSave = findByIdOrThrow(id);
        RideResponseDto rideResponseDto = rideClientService.getRideById(ratingRequestDto.rideId());
        if(rideResponseDto.driverId().compareTo(ratingRequestDto.userId()) != 0) {
            throw new InvalidFieldValueException(
                    messageSource.getMessage(AppConstants.DIFFERENT_DRIVERS_ID,
                            new Object[]{}, LocaleContextHolder.getLocale()));
        }
        ratingMapper.updateDriverRating(ratingRequestDto, ratingToSave);
        DriverRating rating = driverRatingRepository.save(ratingToSave);
        return ratingMapper.toRatingResponseDto(rating);
    }

    @Override
    public void deleteRating(String id) {
        DriverRating rating = findByIdOrThrow(id);
        rating.setDeleted(true);
        driverRatingRepository.save(rating);
    }

    private DriverRating findByIdOrThrow(String id) {
        return driverRatingRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage(AppConstants.RATING_NOT_FOUND,
                                new Object[]{}, LocaleContextHolder.getLocale())));
    }
}
