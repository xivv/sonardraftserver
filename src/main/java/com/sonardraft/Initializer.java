package com.sonardraft;

import org.opencv.core.Core;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class Initializer {

	public static void main(String[] args) {
		// Initialize needed libraries
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		// Start API
		SpringApplication.run(Initializer.class, args);

		// Start all listeners
		Variables.init();

	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/graphql").allowedOrigins("*").allowedMethods("*").allowedHeaders("*");
			}
		};
	}

}
