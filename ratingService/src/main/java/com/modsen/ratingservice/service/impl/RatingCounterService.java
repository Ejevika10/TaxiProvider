package com.modsen.ratingservice.service.impl;

import com.modsen.ratingservice.dto.RatingResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingCounterService {
    public double countRating(List<RatingResponseDto> ratings) {
        if (ratings == null || ratings.isEmpty()) {
            return 0D;
        }
        double sum = 0D;
        for (RatingResponseDto rating : ratings) {
            sum += rating.rating();
        }
        return sum/ratings.size();
    }

}
