package com.crio.jumbogps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication
@ComponentScan("com.crio.jumbogps.controller")
@EnableJpaRepositories("com.crio.jumbogps.repository")
@EntityScan("com.crio.jumbogps.model")
@EnableSwagger2
public class JumbogpsBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(JumbogpsBackendApplication.class, args);
	}

}
