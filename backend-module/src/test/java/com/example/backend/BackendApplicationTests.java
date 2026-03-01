package com.example.backend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.backend.controller.BeneficioController;


@SpringBootTest
class BackendApplicationTests {

	
	@Autowired
	private BeneficioController controller;

	@Test
	@DisplayName("Teste de contexto da aplicação carregado com sucesso")
	void contextLoads() {
		assertTrue(true);
		assertThat(controller).isNotNull();
	}

}
