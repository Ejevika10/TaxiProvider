package com.modsen.ratingservice.service;

import com.modsen.ratingservice.dto.PageDto;
import com.modsen.ratingservice.dto.RatingRequestDto;
import com.modsen.ratingservice.dto.RatingResponseDto;

import java.util.List;
import java.util.UUID;

public interface RatingService {
    String EXCHANGE_NAME = "ratingservice";
    String DRIVER_ROUTING_KEY = "rating.drivers";
    String PASSENGER_ROUTING_KEY = "rating.passengers";

    List<RatingResponseDto> getAllRatings();

    PageDto<RatingResponseDto> getPageRatings(Integer offset, Integer limit);

    List<RatingResponseDto> getAllRatingsByUserId(UUID userID);

    PageDto<RatingResponseDto> getPageRatingsByUserId(UUID userId, Integer offset, Integer limit);

    RatingResponseDto getRatingById(String id);

    RatingResponseDto addRating(RatingRequestDto ratingRequestDto, String bearerToken);

    RatingResponseDto updateRating(String id, RatingRequestDto ratingRequestDto, String bearerToken);

    void deleteRating(String id);

    List<RatingResponseDto> getAllRatingsByRideIdIn(List<Long> rideIds);
}
