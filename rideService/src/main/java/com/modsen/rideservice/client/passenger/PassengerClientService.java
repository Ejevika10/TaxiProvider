package com.modsen.rideservice.client.passenger;

import com.modsen.rideservice.dto.PassengerResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PassengerClientService {
    private final PassengerClient passengerClient;

    public PassengerResponseDto getPassengerById(long passengerId) {
        return passengerClient.getPassengerById(passengerId);
    }
}
