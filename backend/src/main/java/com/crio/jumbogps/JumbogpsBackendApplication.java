package com.crio.jumbogps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan("com.crio.model")
@SpringBootApplication
public class JumbogpsBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(JumbogpsBackendApplication.class, args);
	}

}
