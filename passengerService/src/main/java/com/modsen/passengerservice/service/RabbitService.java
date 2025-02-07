package com.modsen.passengerservice.service;

import com.modsen.passengerservice.dto.UserDeleteRequestDto;
import com.modsen.passengerservice.dto.UserUpdateRequestDto;

public interface RabbitService {
    void sendUpdateMessage(String exchangeName, String routingKey, UserUpdateRequestDto userUpdateRequestDto);

    void sendDeleteMessage(String exchangeName, String routingKey, UserDeleteRequestDto userDeleteRequestDto);
}
