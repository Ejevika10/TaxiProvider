package com.modsen.ratingservice.mapper;

import com.modsen.ratingservice.dto.RatingRequestDto;
import com.modsen.ratingservice.dto.RatingResponseDto;
import com.modsen.ratingservice.model.DriverRating;
import com.modsen.ratingservice.model.PassengerRating;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = RatingMapper.class)
public interface RatingListMapper {
    List<DriverRating> toDriverRatingList(List<RatingRequestDto> ratingRequestDtoList);

    List<PassengerRating> toPassengerRatingList(List<RatingRequestDto> ratingRequestDtoList);

    List<RatingResponseDto> toPassengerRatingResponseDtoList(List<PassengerRating> ratingList);

    List<RatingResponseDto> toDriverRatingResponseDtoList(List<DriverRating> ratingList);

}
