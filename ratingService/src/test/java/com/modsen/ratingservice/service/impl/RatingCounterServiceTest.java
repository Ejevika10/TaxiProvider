package com.modsen.ratingservice.service.impl;

import com.modsen.ratingservice.dto.RatingResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.modsen.ratingservice.util.TestData.AVG_RATING;
import static com.modsen.ratingservice.util.TestData.MIN_RATING;
import static com.modsen.ratingservice.util.TestData.getRatingResponseDtoListEmpty;
import static com.modsen.ratingservice.util.TestData.getRatingResponseDtoListWithDifferentRatings;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RatingCounterServiceTest {

    @InjectMocks
    private RatingCounterService counterService;

    @Test
    void countRating_whenValidData() {
        List<RatingResponseDto> ratings = getRatingResponseDtoListWithDifferentRatings();

        double actual = counterService.countRating(ratings);

        assertEquals(AVG_RATING, actual);
    }

    @Test
    void countRating_whenEmptyData() {
        List<RatingResponseDto> ratings = getRatingResponseDtoListEmpty();

        double actual = counterService.countRating(ratings);

        assertEquals(MIN_RATING, actual);
    }

    @Test
    void countRating_whenNullData() {
        List<RatingResponseDto> ratings = null;

        double actual = counterService.countRating(ratings);

        assertEquals(MIN_RATING, actual);
    }
}