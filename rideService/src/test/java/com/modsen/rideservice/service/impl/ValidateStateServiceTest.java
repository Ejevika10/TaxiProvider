package com.modsen.rideservice.service.impl;

import com.modsen.rideservice.model.RideState;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class ValidateStateServiceTest {

    private final ValidateStateService validator = new ValidateStateService();

    @ParameterizedTest
    @EnumSource(value = RideState.class, names = {"ACCEPTED", "CANCELLED"})
    void validateState_whenCurrentStateIsCreatedAndCheckedStateIsValid_thenReturnTrue(RideState rideState) {
        RideState currentState = RideState.CREATED;

        assertTrue(validator.validateState(currentState, rideState));
    }

    @ParameterizedTest
    @EnumSource(value = RideState.class, names = {"CREATED", "ON_THE_WAY_TO_PICK_UP_THE_PASSENGER", "ON_THE_WAY_TO_THE_DESTINATION", "COMPLETED"})
    void validateState_whenCurrentStateIsCreatedAndCheckedStateIsInvalid_thenReturnFalse(RideState rideState) {
        RideState currentState = RideState.CREATED;

        assertFalse(validator.validateState(currentState, rideState));
    }

    @ParameterizedTest
    @EnumSource(value = RideState.class, names = {"ON_THE_WAY_TO_PICK_UP_THE_PASSENGER", "CANCELLED"})
    void validateState_whenCurrentStateIsAcceptedAndCheckedStateIsValid_thenReturnTrue(RideState rideState) {
        RideState currentState = RideState.ACCEPTED;

        assertTrue(validator.validateState(currentState, rideState));
    }

    @ParameterizedTest
    @EnumSource(value = RideState.class, names = {"CREATED", "ACCEPTED", "ON_THE_WAY_TO_THE_DESTINATION", "COMPLETED"})
    void validateState_whenCurrentStateIsAcceptedAndCheckedStateIsInvalid_thenReturnFalse(RideState rideState) {
        RideState currentState = RideState.ACCEPTED;

        assertFalse(validator.validateState(currentState, rideState));
    }
    @ParameterizedTest
    @EnumSource(value = RideState.class, names = {"ON_THE_WAY_TO_THE_DESTINATION", "CANCELLED"})
    void validateState_whenCurrentStateIsOnTheWayToPassengerAndCheckedStateIsValid_thenReturnTrue(RideState rideState) {
        RideState currentState = RideState.ON_THE_WAY_TO_PICK_UP_THE_PASSENGER;

        assertTrue(validator.validateState(currentState, rideState));
    }

    @ParameterizedTest
    @EnumSource(value = RideState.class, names = {"CREATED", "ACCEPTED", "ON_THE_WAY_TO_PICK_UP_THE_PASSENGER", "COMPLETED"})
    void validateState_whenCurrentStateIsOnTheWayToPassengerAndCheckedStateIsInvalid_thenReturnFalse(RideState rideState) {
        RideState currentState = RideState.ON_THE_WAY_TO_PICK_UP_THE_PASSENGER;

        assertFalse(validator.validateState(currentState, rideState));
    }

    @ParameterizedTest
    @EnumSource(value = RideState.class, names = {"COMPLETED", "CANCELLED"})
    void validateState_whenCurrentStateIsOnTheWayToDestinationAndCheckedStateIsValid_thenReturnTrue(RideState rideState) {
        RideState currentState = RideState.ON_THE_WAY_TO_THE_DESTINATION;

        assertTrue(validator.validateState(currentState, rideState));
    }

    @ParameterizedTest
    @EnumSource(value = RideState.class, names = {"CREATED", "ACCEPTED", "ON_THE_WAY_TO_PICK_UP_THE_PASSENGER", "ON_THE_WAY_TO_THE_DESTINATION"})
    void validateState_whenCurrentStateIsOnTheWayToDestinationAndCheckedStateIsInvalid_thenReturnFalse(RideState rideState) {
        RideState currentState = RideState.ON_THE_WAY_TO_THE_DESTINATION;

        assertFalse(validator.validateState(currentState, rideState));
    }

    @ParameterizedTest
    @EnumSource(value = RideState.class, names = {"CREATED", "ACCEPTED", "ON_THE_WAY_TO_PICK_UP_THE_PASSENGER", "ON_THE_WAY_TO_THE_DESTINATION", "COMPLETED"})
    void validateState_whenCurrentStateIsCompletedAndCheckedStateIsInvalid_thenReturnFalse(RideState rideState) {
        RideState currentState = RideState.COMPLETED;

        assertFalse(validator.validateState(currentState, rideState));
    }

    @ParameterizedTest
    @EnumSource(value = RideState.class, names = {"CREATED", "ACCEPTED", "ON_THE_WAY_TO_PICK_UP_THE_PASSENGER", "ON_THE_WAY_TO_THE_DESTINATION", "COMPLETED"})
    void validateState_whenCurrentStateIsCancelledAndCheckedStateIsInvalid_thenReturnFalse(RideState rideState) {
        RideState currentState = RideState.CANCELLED;

        assertFalse(validator.validateState(currentState, rideState));
    }
}