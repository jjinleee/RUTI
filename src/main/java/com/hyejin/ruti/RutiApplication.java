package com.hyejin.ruti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RutiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RutiApplication.class, args);
	}

}
