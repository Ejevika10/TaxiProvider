package com.modsen.rideservice.service.impl;

import com.modsen.rideservice.model.RideState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ValidateStateServiceTest {

    @InjectMocks
    private ValidateStateService validator;

    @Test
    void validateState_whenCreated() {
        RideState currentState = RideState.CREATED;

        assertFalse(validator.validateState(currentState, RideState.CREATED));
        assertTrue(validator.validateState(currentState, RideState.ACCEPTED));
        assertFalse(validator.validateState(currentState, RideState.ON_THE_WAY_TO_PICK_UP_THE_PASSENGER));
        assertFalse(validator.validateState(currentState, RideState.ON_THE_WAY_TO_THE_DESTINATION));
        assertFalse(validator.validateState(currentState, RideState.COMPLETED));
    }

    @Test
    void validateState_whenAccepted() {
        RideState currentState = RideState.ACCEPTED;

        assertFalse(validator.validateState(currentState, RideState.CREATED));
        assertFalse(validator.validateState(currentState, RideState.ACCEPTED));
        assertTrue(validator.validateState(currentState, RideState.ON_THE_WAY_TO_PICK_UP_THE_PASSENGER));
        assertFalse(validator.validateState(currentState, RideState.ON_THE_WAY_TO_THE_DESTINATION));
        assertFalse(validator.validateState(currentState, RideState.COMPLETED));
    }

    @Test
    void validateState_whenOnTheWayToPassenger() {
        RideState currentState = RideState.ON_THE_WAY_TO_PICK_UP_THE_PASSENGER;

        assertFalse(validator.validateState(currentState, RideState.CREATED));
        assertFalse(validator.validateState(currentState, RideState.ACCEPTED));
        assertFalse(validator.validateState(currentState, RideState.ON_THE_WAY_TO_PICK_UP_THE_PASSENGER));
        assertTrue(validator.validateState(currentState, RideState.ON_THE_WAY_TO_THE_DESTINATION));
        assertFalse(validator.validateState(currentState, RideState.COMPLETED));
    }

    @Test
    void validateState_whenOnTheWayToDestination() {
        RideState currentState = RideState.ON_THE_WAY_TO_THE_DESTINATION;

        assertFalse(validator.validateState(currentState, RideState.CREATED));
        assertFalse(validator.validateState(currentState, RideState.ACCEPTED));
        assertFalse(validator.validateState(currentState, RideState.ON_THE_WAY_TO_PICK_UP_THE_PASSENGER));
        assertFalse(validator.validateState(currentState, RideState.ON_THE_WAY_TO_THE_DESTINATION));
        assertTrue(validator.validateState(currentState, RideState.COMPLETED));
    }

    @Test
    void validateState_whenCompleted() {
        RideState currentState = RideState.COMPLETED;

        assertFalse(validator.validateState(currentState, RideState.CREATED));
        assertFalse(validator.validateState(currentState, RideState.ACCEPTED));
        assertFalse(validator.validateState(currentState, RideState.ON_THE_WAY_TO_PICK_UP_THE_PASSENGER));
        assertFalse(validator.validateState(currentState, RideState.ON_THE_WAY_TO_THE_DESTINATION));
        assertFalse(validator.validateState(currentState, RideState.COMPLETED));
    }

    @Test
    void validateState_whenCheckedStateIsCancelled() {
        RideState checkedState = RideState.CANCELLED;

        assertTrue(validator.validateState(RideState.CREATED, checkedState));
        assertTrue(validator.validateState(RideState.ACCEPTED, checkedState));
        assertTrue(validator.validateState(RideState.ON_THE_WAY_TO_PICK_UP_THE_PASSENGER, checkedState));
        assertTrue(validator.validateState(RideState.ON_THE_WAY_TO_THE_DESTINATION, checkedState));
        assertFalse(validator.validateState(RideState.COMPLETED, checkedState));
    }
}