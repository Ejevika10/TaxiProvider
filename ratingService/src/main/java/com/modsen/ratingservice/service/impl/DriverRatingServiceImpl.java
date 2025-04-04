package com.modsen.ratingservice.service.impl;

import com.modsen.exceptionstarter.exception.NotFoundException;
import com.modsen.ratingservice.dto.PageDto;
import com.modsen.ratingservice.dto.RatingRequestDto;
import com.modsen.ratingservice.dto.RatingResponseDto;
import com.modsen.ratingservice.dto.UserRatingDto;
import com.modsen.ratingservice.mapper.PageMapper;
import com.modsen.ratingservice.mapper.RatingListMapper;
import com.modsen.ratingservice.mapper.RatingMapper;
import com.modsen.ratingservice.model.DriverRating;
import com.modsen.ratingservice.repository.DriverRatingRepository;
import com.modsen.ratingservice.service.RabbitService;
import com.modsen.ratingservice.service.RatingService;
import com.modsen.ratingservice.util.MessageConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.modsen.ratingservice.util.AppConstants.DRIVER_RATING_CACHE_NAME;

@Service
@RequiredArgsConstructor
@Qualifier("DriverRatingServiceImpl")
public class DriverRatingServiceImpl implements RatingService {

    private final DriverRatingRepository driverRatingRepository;
    private final RatingMapper ratingMapper;
    private final RatingListMapper ratingListMapper;
    private final PageMapper pageMapper;
    private final RabbitService rabbitService;
    private final RatingCounterService ratingCounterService;
    private final DriverValidatorService validator;

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
    public List<RatingResponseDto> getAllRatingsByUserId(UUID userId) {
        List<DriverRating> ratings = driverRatingRepository.findAllByUserIdAndDeletedIsFalse(userId);
        return ratingListMapper.toDriverRatingResponseDtoList(ratings);
    }

    @Override
    public PageDto<RatingResponseDto> getPageRatingsByUserId(UUID userId, Integer offset, Integer limit) {
        Page<RatingResponseDto> pageRating = driverRatingRepository
                .findAllByUserIdAndDeletedIsFalse(userId, PageRequest.of(offset, limit))
                .map(ratingMapper::toRatingResponseDto);
        return pageMapper.pageToDto(pageRating);
    }

    @Override
    @Cacheable(value = DRIVER_RATING_CACHE_NAME, key = "#id")
    public RatingResponseDto getRatingById(String id) {
        DriverRating rating = findByIdOrThrow(id);
        return ratingMapper.toRatingResponseDto(rating);
    }

    @Override
    @CachePut(value = DRIVER_RATING_CACHE_NAME, key = "#result.id()")
    public RatingResponseDto addRating(RatingRequestDto ratingRequestDto, String bearerToken) {
        validator.validateForCreate(ratingRequestDto, bearerToken);
        DriverRating ratingToSave = ratingMapper.toDriverRating(ratingRequestDto);
        ratingToSave.setDeleted(false);
        DriverRating rating = driverRatingRepository.save(ratingToSave);
        updateAverageRating(rating.getUserId());
        return ratingMapper.toRatingResponseDto(rating);
    }

    @Override
    @CachePut(value = DRIVER_RATING_CACHE_NAME, key = "#id")
    public RatingResponseDto updateRating(String id, RatingRequestDto ratingRequestDto, String bearerToken) {
        DriverRating ratingToSave = findByIdOrThrow(id);
        validator.validateForUpdate(ratingRequestDto, bearerToken);
        ratingMapper.updateDriverRating(ratingRequestDto, ratingToSave);
        DriverRating rating = driverRatingRepository.save(ratingToSave);
        updateAverageRating(rating.getUserId());
        return ratingMapper.toRatingResponseDto(rating);
    }

    @Override
    @CacheEvict(value = DRIVER_RATING_CACHE_NAME, key = "#id")
    public void deleteRating(String id) {
        DriverRating rating = findByIdOrThrow(id);
        rating.setDeleted(true);
        driverRatingRepository.save(rating);
    }

    private DriverRating findByIdOrThrow(String id) {
        return driverRatingRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new NotFoundException(MessageConstants.RATING_NOT_FOUND));
    }

    private void updateAverageRating(UUID userId){
        List<DriverRating> ratings = driverRatingRepository.findTop100ByUserIdAndDeletedIsFalse(userId);
        List<RatingResponseDto> ratingDtos = ratingListMapper.toDriverRatingResponseDtoList(ratings);
        double averageRating = ratingCounterService.countRating(ratingDtos);
        UserRatingDto userRatingDto = new UserRatingDto(userId, averageRating);
        rabbitService.sendMessage(EXCHANGE_NAME,DRIVER_ROUTING_KEY, userRatingDto);
    }

    public List<RatingResponseDto> getAllRatingsByRideIdIn(List<Long> rideIds) {
        List<DriverRating> ratings = driverRatingRepository.findAllByRideIdIsInAndDeletedIsFalse(rideIds);
        return ratingListMapper.toDriverRatingResponseDtoList(ratings);
    }
}
