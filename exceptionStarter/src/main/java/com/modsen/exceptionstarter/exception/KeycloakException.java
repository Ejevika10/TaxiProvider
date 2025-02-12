package com.modsen.exceptionstarter.exception;

import com.modsen.exceptionstarter.message.ErrorMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KeycloakException extends RuntimeException {
    private ErrorMessage errorMessage;
}
