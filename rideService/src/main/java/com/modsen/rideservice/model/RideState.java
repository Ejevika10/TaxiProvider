package com.modsen.rideservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.modsen.exceptionstarter.exception.InvalidFieldValueException;
import com.modsen.rideservice.util.MessageConstants;

public enum RideState {
    CREATED("Created"),
    ACCEPTED("Accepted"),
    ON_THE_WAY_TO_PICK_UP_THE_PASSENGER("On the way to pick up the passenger"),
    ON_THE_WAY_TO_THE_DESTINATION("On the way to the destination"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String rideState;

    RideState(String state) {
        this.rideState = state;
    }

    @JsonValue
    public String getState() {
        return rideState;
    }

    @JsonCreator
    public static RideState fromValue(String value) {
        for (RideState state : values()) {
            String currentState = state.getState();
            if (currentState.equalsIgnoreCase(value)) {
                return state;
            }
        }
        throw new InvalidFieldValueException(MessageConstants.INVALID_STATE_VALUE);
    }
}
