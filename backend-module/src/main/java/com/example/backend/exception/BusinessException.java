package com.example.backend.exception;

/* Exceção personalizada para erros de negócio. */
public class BusinessException extends RuntimeException {
    public BusinessException(String mensagem) {
        super(mensagem);
    }
    public BusinessException(String mensagem, Object ... params) {
        super(String.format(mensagem, params));
    }
}
