package com.hiruna.dm2_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class Dm2BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(Dm2BackendApplication.class, args);
	}

}
