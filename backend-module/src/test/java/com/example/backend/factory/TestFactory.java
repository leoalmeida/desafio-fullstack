package com.example.backend.factory;

import com.example.backend.dto.BeneficioRequestDto;
import com.example.backend.dto.BeneficioResponseDto;
import com.example.ejb.entity.Beneficio;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class TestFactory {

    public static final String BENEFICIOS_API_ENDPOINT = "/api/v1/beneficios";
    public static final String TRANSFERENCIAS_API_ENDPOINT = "/api/v1/transferencias";

    public static BeneficioResponseDto gerarBeneficioResponseDto(boolean ativo) {
        Long regBeneficio = (long) (Math.random() * 999) + 1;
        BeneficioResponseDto produto = BeneficioResponseDto.builder()
                .id(regBeneficio)
                .nome("Beneficio " + regBeneficio)
                .descricao("Descrição da Beneficio " + regBeneficio)
                .valor(new BigDecimal(Math.random() * 1000).setScale(2, RoundingMode.HALF_UP))
                .ativo(ativo)
                .build();
        return produto;
    }

    public static BeneficioRequestDto gerarBeneficioRequestDto(boolean ativo) {
        Long regBeneficio = (long) (Math.random() * 999) + 1;
        BeneficioRequestDto produto = gerarBeneficioRequestDto(ativo, new BigDecimal(Math.random() * 1000).setScale(2, RoundingMode.HALF_UP));
        return produto;
    }
    
    public static BeneficioRequestDto gerarBeneficioRequestDto(boolean ativo, BigDecimal valor) {
        Long regBeneficio = (long) (Math.random() * 999) + 1;
        BeneficioRequestDto produto = BeneficioRequestDto.builder()
                .nome("Beneficio " + regBeneficio)
                .descricao("Descrição da Beneficio " + regBeneficio)
                .valor(valor)
                .ativo(ativo)
                .build();
        return produto;
    }

    public static Beneficio gerarBeneficio() {
        Long regBeneficio = (long) (Math.random() * 999) + 1;
        Beneficio produto = Beneficio.builder()
                .id(regBeneficio)
                .nome("Beneficio " + regBeneficio)
                .descricao("Descrição da Beneficio " + regBeneficio)
                .valor(new BigDecimal(Math.random() * 1000).setScale(2, RoundingMode.HALF_UP))
                .ativo(Math.random() < 0.5) // 50% de chance de ser true
                .version(1L)
                .build();
        return produto;
    }

    public static Beneficio gerarBeneficio(boolean ativo) {
        Beneficio produto = gerarBeneficio(ativo, new BigDecimal(Math.random() * 1000).setScale(2, RoundingMode.HALF_UP));
        return produto;
    }

    public static Beneficio gerarBeneficio(boolean ativo, BigDecimal valor) {
        Long regBeneficio = (long) (Math.random() * 999) + 1;
        Beneficio produto = Beneficio.builder()
                .id(regBeneficio)
                .nome("Beneficio " + regBeneficio)
                .descricao("Descrição da Beneficio " + regBeneficio)
                .valor(valor)
                .ativo(ativo)
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
