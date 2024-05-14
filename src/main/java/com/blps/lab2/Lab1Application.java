package com.blps.lab2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
// @Configuration
// @ImportResource("classpath:context.xml")
public class Lab1Application extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(Lab1Application.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Lab1Application.class);
	}
}
