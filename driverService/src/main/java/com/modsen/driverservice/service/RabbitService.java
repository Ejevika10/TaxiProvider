package com.modsen.driverservice.service;

import com.modsen.driverservice.dto.UserDeleteRequestDto;
import com.modsen.driverservice.dto.UserUpdateRequestDto;

public interface RabbitService {
    void sendUpdateMessage(String exchangeName, String routingKey, UserUpdateRequestDto userUpdateRequestDto);

    void sendDeleteMessage(String exchangeName, String routingKey, UserDeleteRequestDto userDeleteRequestDto);

}
