package com.modsen.reportservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.modsen.exceptionstarter.exception.InvalidFieldValueException;
import com.modsen.reportservice.util.MessageConstants;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {
    DRIVER("DRIVER"),
    PASSENGER("PASSENGER"),
    ADMIN("ADMIN");

    private final String role;

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
