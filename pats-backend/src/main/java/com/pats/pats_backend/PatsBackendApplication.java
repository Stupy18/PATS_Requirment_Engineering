package com.pats.pats_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PatsBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PatsBackendApplication.class, args);
	}

}
