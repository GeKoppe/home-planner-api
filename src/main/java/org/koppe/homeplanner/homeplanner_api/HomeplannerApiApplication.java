package org.koppe.homeplanner.homeplanner_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class HomeplannerApiApplication {

	/**
	 * Main method to start the api
	 * 
	 * @param args Various java arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(HomeplannerApiApplication.class, args);
	}

}
