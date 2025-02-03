package com.modsen.authservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KeycloakException extends RuntimeException {
    private ErrorMessage errorMessage;
}
