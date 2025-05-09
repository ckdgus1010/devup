package com.upstage.devup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class DevupApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevupApplication.class, args);
	}

}
