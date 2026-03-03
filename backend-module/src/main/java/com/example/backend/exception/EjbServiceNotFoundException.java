package com.example.backend.exception;

import com.example.ejb.exception.BusinessException;

/* Exceção personalizada para erros de negócio. */
public class EjbServiceNotFoundException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public EjbServiceNotFoundException(final String message) {
        super(message);
    }

    public EjbServiceNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
