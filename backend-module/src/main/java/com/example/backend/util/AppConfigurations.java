package com.example.backend.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.example.ejb.BeneficioEjbService;

import jakarta.persistence.EntityManager;

@Configuration
public class AppConfigurations {

    /**
     * Configura o BeneficioEjbService para ser gerenciado pelo Spring, permitindo a injeção de dependências.
     * O EntityManager é injetado manualmente usando reflection, já que o EJB não é um componente Spring tradicional.
     * 
     * @param em EntityManager gerenciado pelo Spring para acesso ao banco de dados
     * @return Instância configurada do BeneficioEjbService com EntityManager injetado
     */
    @Bean
    public BeneficioEjbService beneficioEjbService(EntityManager em) {
        BeneficioEjbService ejb = new BeneficioEjbService();
        
        try {
            Field field = BeneficioEjbService.class.getDeclaredField("em");
            field.setAccessible(true);
            field.set(ejb, em);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Não foi possível iniciar EJB service", e);
        }
        
        return ejb;
    }

    /* Configuração global de CORS para permitir requisições do frontend Angular. 
     *  Permite todas as origens, métodos e headers para facilitar o desenvolvimento local. 
     *  Em produção, recomenda-se restringir as origens e métodos permitidos para maior segurança. 
     */
    @Bean
    public CorsFilter cors() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        org.springframework.web.cors.CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();
        
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
