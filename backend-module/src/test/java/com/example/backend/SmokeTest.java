package com.example.backend;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;


@SpringBootTest
@ContextConfiguration(classes = BackendApplication.class)
@ActiveProfiles("test")
public class SmokeTest {

	@Autowired
	private BeneficioController beneficioController;

	@Test
	void contextLoads() throws Exception {
		assertThat(beneficioController).isNotNull();
	}
}