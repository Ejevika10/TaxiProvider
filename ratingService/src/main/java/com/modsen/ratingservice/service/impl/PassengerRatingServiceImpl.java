package com.modsen.ratingservice.service.impl;

import com.modsen.ratingservice.dto.PageDto;
import com.modsen.ratingservice.dto.RatingRequestDto;
import com.modsen.ratingservice.dto.RatingResponseDto;
import com.modsen.ratingservice.dto.UserRatingDto;
import com.modsen.ratingservice.exception.NotFoundException;
import com.modsen.ratingservice.mapper.PageMapper;
import com.modsen.ratingservice.mapper.RatingListMapper;
import com.modsen.ratingservice.mapper.RatingMapper;
import com.modsen.ratingservice.model.PassengerRating;
import com.modsen.ratingservice.repository.PassengerRatingRepository;
import com.modsen.ratingservice.service.RabbitService;
import com.modsen.ratingservice.service.RatingService;
import com.modsen.ratingservice.util.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Qualifier("PassengerRatingServiceImpl")
public class PassengerRatingServiceImpl implements RatingService {

    private final PassengerRatingRepository passengerRatingRepository;
    private final RatingMapper ratingMapper;
    private final RatingListMapper ratingListMapper;
    private final PageMapper pageMapper;
    private final RabbitService rabbitService;
    private final RatingCounterService ratingCounterService;
    private final PassengerValidatorService validator;

    @Override
    public List<RatingResponseDto> getAllRatings() {
        List<PassengerRating> ratings = passengerRatingRepository.findAllByDeletedIsFalse();
        return ratingListMapper.toPassengerRatingResponseDtoList(ratings);
    }

    @Override
    public PageDto<RatingResponseDto> getPageRatings(Integer offset, Integer limit) {
        Page<RatingResponseDto> pageRating = passengerRatingRepository.findAllByDeletedIsFalse(PageRequest.of(offset, limit))
                .map(ratingMapper::toRatingResponseDto);
        return pageMapper.pageToDto(pageRating);
    }

    @Override
    public List<RatingResponseDto> getAllRatingsByUserId(Long userId) {
        List<PassengerRating> ratings = passengerRatingRepository.findAllByUserIdAndDeletedIsFalse(userId);
        return ratingListMapper.toPassengerRatingResponseDtoList(ratings);
    }

    @Override
    public PageDto<RatingResponseDto> getPageRatingsByUserId(Long userId, Integer offset, Integer limit) {
        Page<RatingResponseDto> pageRating = passengerRatingRepository
                .findAllByUserIdAndDeletedIsFalse(userId, PageRequest.of(offset, limit))
                .map(ratingMapper::toRatingResponseDto);
        return pageMapper.pageToDto(pageRating);
    }

    @Override
    public RatingResponseDto getRatingById(String id) {
        PassengerRating rating = findByIdOrThrow(id);
        return ratingMapper.toRatingResponseDto(rating);
    }

    @Override
    public RatingResponseDto addRating(RatingRequestDto ratingRequestDto) {
        validator.validateForCreate(ratingRequestDto);
        PassengerRating ratingToSave = ratingMapper.toPassengerRating(ratingRequestDto);
        ratingToSave.setDeleted(false);
        PassengerRating rating = passengerRatingRepository.save(ratingToSave);
        updateAverageRating(rating.getUserId());
        return ratingMapper.toRatingResponseDto(rating);
    }

    @Override
    public RatingResponseDto updateRating(String id, RatingRequestDto ratingRequestDto) {
        PassengerRating ratingToSave = findByIdOrThrow(id);
        validator.validateForUpdate(ratingRequestDto);
        ratingMapper.updatePassengerRating(ratingRequestDto, ratingToSave);
        PassengerRating rating = passengerRatingRepository.save(ratingToSave);
        updateAverageRating(rating.getUserId());
        return ratingMapper.toRatingResponseDto(rating);
    }

    @Override
    public void deleteRating(String id) {
        PassengerRating rating = findByIdOrThrow(id);
        rating.setDeleted(true);
        passengerRatingRepository.save(rating);
    }

    private PassengerRating findByIdOrThrow(String id) {
        return passengerRatingRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new NotFoundException(AppConstants.RATING_NOT_FOUND));
    }

    private void updateAverageRating(long userId){
        List<PassengerRating> ratings = passengerRatingRepository.findTop40ByUserIdAndDeletedIsFalse(userId);
        List<RatingResponseDto> ratingDtos = ratingListMapper.toPassengerRatingResponseDtoList(ratings);
        double averageRating = ratingCounterService.countRating(ratingDtos);
        UserRatingDto userRatingDto = new UserRatingDto(userId, averageRating);
        rabbitService.sendMessage(EXCHANGE_NAME,PASSENGER_ROUTING_KEY, userRatingDto);
    }
}

