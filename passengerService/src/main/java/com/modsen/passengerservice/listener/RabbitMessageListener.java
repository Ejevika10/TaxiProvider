package com.modsen.passengerservice.listener;

import com.modsen.passengerservice.dto.PassengerResponseDto;
import com.modsen.passengerservice.dto.UserRatingDto;
import com.modsen.passengerservice.service.PassengerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMessageListener {

    private final PassengerService passengerService;

    @RabbitListener(queues = "${spring.rabbitmq.passenger.queue}")
    public void listen(UserRatingDto userRatingDto) {
        log.info("Received message: {}", userRatingDto.toString());
        PassengerResponseDto passenger = passengerService.updateRating(userRatingDto);
        log.info("Updated passenger: {}", passenger.toString());
    }
}
