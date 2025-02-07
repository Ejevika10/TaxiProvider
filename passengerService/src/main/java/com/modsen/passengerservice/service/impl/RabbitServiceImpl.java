package com.modsen.passengerservice.service.impl;

import com.modsen.passengerservice.dto.UserDeleteRequestDto;
import com.modsen.passengerservice.dto.UserUpdateRequestDto;
import com.modsen.passengerservice.service.RabbitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitServiceImpl implements RabbitService {

    private final AmqpTemplate amqpTemplate;

    @Override
    public void sendUpdateMessage(String exchangeName, String routingKey, UserUpdateRequestDto userUpdateRequestDto) {
        log.info("Sent message: {}", userUpdateRequestDto.toString());
        amqpTemplate.convertAndSend(exchangeName, routingKey, userUpdateRequestDto);
    }

    @Override
    public void sendDeleteMessage(String exchangeName, String routingKey, UserDeleteRequestDto userDeleteRequestDto) {
        log.info("Sent message: {}", userDeleteRequestDto.toString());
        amqpTemplate.convertAndSend(exchangeName, routingKey, userDeleteRequestDto);
    }
}
