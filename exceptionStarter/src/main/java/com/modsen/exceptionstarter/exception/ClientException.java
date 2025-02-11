package com.modsen.exceptionstarter.exception;

import com.modsen.exceptionstarter.message.ErrorMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClientException extends RuntimeException {
    private ErrorMessage errorMessage;
}
