package com.modsen.ratingservice.client.ride;

import com.modsen.ratingservice.dto.RideResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RideClientService {
    private final RideClient rideClient;

    public RideResponseDto getRideById(long rideId) {
        return rideClient.getRideById(rideId);
    }
}
