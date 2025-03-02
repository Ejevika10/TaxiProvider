package com.modsen.authservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.modsen.authservice.util.MessageConstants;
import com.modsen.exceptionstarter.exception.InvalidFieldValueException;

public enum Role {
    DRIVER("driver"),
    PASSENGER("passenger"),
    ADMIN("admin");

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
