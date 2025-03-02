package com.modsen.passengerservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.modsen.exceptionstarter.exception.InvalidFieldValueException;
import com.modsen.passengerservice.util.MessageConstants;

public enum Role {
    PASSENGER("PASSENGER"),
    ADMIN("ADMIN");

    private final String role;

    Role(String role) {
        this.role = role;
    }

    @JsonValue
    public String getRole() {
        return role;
    }

    @JsonCreator
    public static Role fromValue(String value) {
        for (Role state : values()) {
            String currentState = state.getRole();
            if (currentState.equalsIgnoreCase(value)) {
                return state;
            }
        }
        throw new InvalidFieldValueException(MessageConstants.INVALID_ROLE_VALUE);
    }
}
