package com.example.backend.validator;

import com.example.backend.dto.BeneficioRequestDto;

public class BeneficioValidator extends ObjectsValidator<BeneficioRequestDto> {
    
    public static final int NOME_MIN_LENGTH = 3; 
    public static final int NOME_MAX_LENGTH = 100; 
    public static final int DESCRICAO_MAX_LENGTH = 255; 
    public static final String MIN_VALOR_BENEFICIO = "0.00";
    public static final String MIN_VALOR_TRANSFERENCIA = "0.01";
    
}
