package com.sonardraft;

import java.io.File;

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

		// testSingle("bard.png");
		// startProgramm();
	}

	public static void startProgramm() {

		// Checks if league client is running
		// @TODO: Check if draft phase or in game or in menu
		Thread listeners = new Thread() {

			public void run() {
				while (Tools.programmRunning) {
					Tools.isClientRunning();

				}
			}
		};

		listeners.start();

		// Core logic
		// Take screenshots then analyse
		Thread programm = new Thread() {

			public void run() {
				Tools.start();
			}
		};

		programm.start();
	}

	public static void testSingle(String name) {
		System.out.println(TemplateRecognition.featureMatchingSimple(new File(Variables.SCREENPATH + name)).getName());
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/graphql").allowedOrigins("*").allowedMethods("*").allowedHeaders("*");
			}
		};
	}

}
