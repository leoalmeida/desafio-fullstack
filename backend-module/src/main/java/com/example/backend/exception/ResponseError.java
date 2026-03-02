package com.example.backend.exception;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* Classe de modelo para representar erros de resposta. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseError {
    public static final String STATUS_ERROR = "error";
    public static final String STATUS_SUCCESS = "success";
    public static final int STATUS_CODE_SUCCESS = 200;
    public static final int STATUS_CODE_ERROR = 400;
    

    private Date timestamp = new Date();
    private String status = STATUS_ERROR;
    private int statusCode = STATUS_CODE_ERROR;
    private String error;

}
