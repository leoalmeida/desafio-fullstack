package com.example.backend.factory;

import java.math.BigDecimal;
import java.text.Normalizer;

import java.util.regex.Pattern;

import com.example.backend.dto.BeneficioDto;
import com.example.ejb.entity.Beneficio;

public class TestFactory {

    public static final String BENEFICIOS_API_ENDPOINT = "/api/v1/beneficios";
    
    public static BeneficioDto gerarBeneficioDto() {
        Integer regBeneficio = (int)(Math.random() * 999) + 1;
        BeneficioDto produto = BeneficioDto.builder()
                .nome("Beneficio "+regBeneficio)
                .descricao("Descrição da Beneficio " + regBeneficio)
                .valor(new BigDecimal(Math.random()*1000))
                .ativo(Math.random() < 0.5) //50% de chance de ser true     
                .build();
        return produto; 
    }

    public static Beneficio gerarBeneficio() {
        Integer regBeneficio = (int)(Math.random() * 999) + 1;
        Beneficio produto = Beneficio.builder()
                .nome("Beneficio "+regBeneficio)
                .descricao("Descrição da Beneficio " + regBeneficio)
                .valor(new BigDecimal(Math.random()*1000))
                .ativo(Math.random() < 0.5) //50% de chance de ser true     
                .version(1L)
                .build();
        return produto;
    }

    public static String removerAcentos(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }
}
