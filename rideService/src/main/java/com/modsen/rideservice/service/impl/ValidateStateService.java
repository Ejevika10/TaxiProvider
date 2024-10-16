package com.modsen.rideservice.service.impl;

import com.modsen.rideservice.model.RideState;
import org.springframework.stereotype.Component;

@Component
public class ValidateStateService {
    boolean validateState(RideState currentState, RideState checkedState) {
        return switch (currentState) {
            case CREATED -> checkedState == RideState.ACCEPTED || checkedState == RideState.CANCELLED;
            case ACCEPTED ->
                    checkedState == RideState.ON_THE_WAY_TO_PICK_UP_THE_PASSENGER || checkedState == RideState.CANCELLED;
            case ON_THE_WAY_TO_PICK_UP_THE_PASSENGER ->
                    checkedState == RideState.ON_THE_WAY_TO_THE_DESTINATION || checkedState == RideState.CANCELLED;
            case ON_THE_WAY_TO_THE_DESTINATION ->
                    checkedState == RideState.COMPLETED || checkedState == RideState.CANCELLED;
            default -> false;
        };
    }
}
