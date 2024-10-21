package com.modsen.ratingservice.service.impl;

import com.modsen.ratingservice.dto.UserRatingDto;
import com.modsen.ratingservice.service.RabbitService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitServiceImpl implements RabbitService {

    private final AmqpTemplate amqpTemplate;

    @Override
    public void sendMessage(String exchangeName, String routingKey, UserRatingDto userRatingDto) {
        amqpTemplate.convertAndSend(exchangeName, routingKey, userRatingDto);
    }
}