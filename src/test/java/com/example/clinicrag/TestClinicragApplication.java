package com.example.clinicrag;

import org.springframework.boot.SpringApplication;

public class TestClinicragApplication {

	public static void main(String[] args) {
		SpringApplication.from(ClinicragApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
