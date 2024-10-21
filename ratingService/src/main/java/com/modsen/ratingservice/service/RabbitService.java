package com.modsen.ratingservice.service;

import com.modsen.ratingservice.dto.UserRatingDto;

public interface RabbitService {
    void sendMessage(String exchangeName, String routingKey, UserRatingDto userRatingDto);
}
