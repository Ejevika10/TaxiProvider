package com.modsen.driverservice.listener;

import com.modsen.driverservice.dto.DriverResponseDto;
import com.modsen.driverservice.dto.UserRatingDto;
import com.modsen.driverservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMessageListener {

    private final DriverService driverService;

    @RabbitListener(queues = "${spring.rabbitmq.driver.queue}")
    public void listen(Message message, @Payload UserRatingDto userRatingDto) {
        log.info("Received headers: {}", message.getMessageProperties().getHeaders());
        log.info("Received message: {}", userRatingDto.toString());
        DriverResponseDto driverResponseDto = driverService.updateRating(userRatingDto);
        log.info("Updated driver: {}", driverResponseDto.toString());
    }

}
