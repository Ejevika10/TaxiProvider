package com.modsen.ratingservice.mapper;

import com.modsen.ratingservice.dto.RatingRequestDto;
import com.modsen.ratingservice.dto.RatingResponseDto;
import com.modsen.ratingservice.model.DriverRating;
import com.modsen.ratingservice.model.PassengerRating;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RatingMapper {
    RatingResponseDto toRatingResponseDto(PassengerRating rating);
    RatingResponseDto toRatingResponseDto(DriverRating rating);

    RatingRequestDto toRatingRequestDto(PassengerRating rating);
    RatingRequestDto toRatingRequestDto(DriverRating rating);

    PassengerRating toPassengerRating(RatingRequestDto ratingRequestDto);
    DriverRating toDriverRating(RatingRequestDto ratingRequestDto);

    PassengerRating toPassengerRating(RatingResponseDto ratingResponseDto);
    DriverRating toDriverRating(RatingResponseDto ratingResponseDto);
}