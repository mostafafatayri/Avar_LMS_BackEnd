package com.fatayriTech.avarLMS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AvarLmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AvarLmsApplication.class, args);
	}


}
