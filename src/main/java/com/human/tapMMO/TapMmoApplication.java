package com.human.tapMMO;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EntityScan(basePackages = "com.human.tapMMO.model")
public class TapMmoApplication {
	public static void main(String[] args) {
		SpringApplication.run(TapMmoApplication.class, args);
	}
}
