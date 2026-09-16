package comp3011.assignment1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * COMP3011 Assignment 1 - Speech-to-Text Web Application
 * 
 * Entry point for the Spring Boot application.
 * Configures virtual threads for high concurrency and initializes
 * all REST API endpoints for transcription, statistics, and administration.
 */
@SpringBootApplication
public class Assignment1Application {

	public static void main(String[] args) {
		SpringApplication.run(Assignment1Application.class, args);
	}

}
