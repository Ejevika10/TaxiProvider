package com.modsen.ratingservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InvalidStateException extends RuntimeException {
    private String message;
}
