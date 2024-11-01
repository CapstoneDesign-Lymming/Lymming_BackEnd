package com.supernova.lymming;

import com.supernova.lymming.github.config.GithubApiClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LymmingApplication {

	public static void main(String[] args) {
		SpringApplication.run(LymmingApplication.class, args);
	}

	@Bean
	public GithubApiClient gitHubApiClient() {
		return new GithubApiClient();
	}
}

