package com.modsen.passengerservice.listener;

import com.modsen.passengerservice.dto.PassengerResponseDto;
import com.modsen.passengerservice.dto.UserRatingDto;
import com.modsen.passengerservice.service.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMessageListener {
    static final String PASSENGER_QUEUE_NAME = "rating.passengers";

    private final PassengerService passengerService;

    @RabbitListener(queues = PASSENGER_QUEUE_NAME)
    public void listen(UserRatingDto userRatingDto) {
        PassengerResponseDto passenger = passengerService.updateRating(userRatingDto);
        System.out.println(passenger);
    }
}
