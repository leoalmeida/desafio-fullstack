package com.example.backend.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/* Exceção personalizada para erros de negócio. */
@Getter
@Setter
@ToString
public class ControllerException extends Exception {

    private final String errorMessage;

    public ControllerException(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}