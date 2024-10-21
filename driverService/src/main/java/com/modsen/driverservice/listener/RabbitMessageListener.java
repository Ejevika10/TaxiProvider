package com.modsen.driverservice.listener;

import com.modsen.driverservice.dto.DriverResponseDto;
import com.modsen.driverservice.dto.UserRatingDto;
import com.modsen.driverservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMessageListener {
    static final String DRIVER_QUEUE_NAME = "rating.drivers";

    private final DriverService driverService;

    @RabbitListener(queues = DRIVER_QUEUE_NAME)
    public void listen(UserRatingDto userRatingDto) {
        DriverResponseDto driverResponseDto = driverService.updateRating(userRatingDto);
        System.out.println(driverResponseDto);
    }

}
