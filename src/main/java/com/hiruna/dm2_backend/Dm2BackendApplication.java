package com.hiruna.dm2_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableRetry
@SpringBootApplication
public class Dm2BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(Dm2BackendApplication.class, args);
	}

}
