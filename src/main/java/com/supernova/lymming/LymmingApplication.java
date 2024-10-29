package com.supernova.lymming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class LymmingApplication {

	public static void main(String[] args) {
		SpringApplication.run(LymmingApplication.class, args);
	}

}
