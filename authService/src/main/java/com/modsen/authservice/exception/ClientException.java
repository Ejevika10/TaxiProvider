package com.modsen.authservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClientException extends RuntimeException {
    private ErrorMessage errorMessage;
}
