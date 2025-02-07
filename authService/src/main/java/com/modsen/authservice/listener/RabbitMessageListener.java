package com.modsen.authservice.listener;

import com.modsen.authservice.dto.UserDeleteRequestDto;
import com.modsen.authservice.dto.UserUpdateRequestDto;
import com.modsen.authservice.service.AuthService;
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

    private final AuthService authService;

    @RabbitListener(queues = "${spring.rabbitmq.user.update.queue}")
    public void listenUpdate(Message message, @Payload UserUpdateRequestDto userUpdateRequestDto) {
        log.info("Received headers: {}", message.getMessageProperties().getHeaders());
        log.info("Received message: {}", userUpdateRequestDto.toString());
        authService.updateUser(userUpdateRequestDto);
    }

    @RabbitListener(queues = "${spring.rabbitmq.user.delete.queue}")
    public void listenDelete(Message message, @Payload UserDeleteRequestDto userDeleteRequestDto) {
        log.info("Received headers: {}", message.getMessageProperties().getHeaders());
        log.info("Received message: {}", userDeleteRequestDto.toString());
        authService.deleteUser(userDeleteRequestDto);
    }
}
