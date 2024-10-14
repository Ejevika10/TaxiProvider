package com.modsen.ratingservice.service;

import com.modsen.ratingservice.dto.PageDto;
import com.modsen.ratingservice.dto.RatingRequestDto;
import com.modsen.ratingservice.dto.RatingResponseDto;

import java.util.List;

public interface RatingService {
    List<RatingResponseDto> getAllRatings();
    PageDto<RatingResponseDto> getPageRatings(Integer offset, Integer limit);

    List<RatingResponseDto> getAllRatingsByUserId(Long userID);
    PageDto<RatingResponseDto> getPageRatingsByUserId(Long userId, Integer offset, Integer limit);

    RatingResponseDto getRatingById(String id);
    RatingResponseDto addRating(RatingRequestDto ratingRequestDto);
    RatingResponseDto updateRating(String id, RatingRequestDto ratingRequestDto);
    void deleteRating(String id);
}
