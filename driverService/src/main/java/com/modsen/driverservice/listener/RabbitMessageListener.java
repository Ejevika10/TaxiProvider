package com.modsen.driverservice.listener;

import com.modsen.driverservice.dto.DriverResponseDto;
import com.modsen.driverservice.dto.UserRatingDto;
import com.modsen.driverservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMessageListener {

    private final DriverService driverService;

    @RabbitListener(queues = "${spring.rabbitmq.driver.queue}")
    public void listen(UserRatingDto userRatingDto) {
        log.info("Received message: {}", userRatingDto.toString());
        DriverResponseDto driverResponseDto = driverService.updateRating(userRatingDto);
        log.info("Updated driver: {}", driverResponseDto.toString());
    }

}
