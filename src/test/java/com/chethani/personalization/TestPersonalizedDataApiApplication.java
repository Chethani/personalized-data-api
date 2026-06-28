package com.chethani.personalization;

import org.springframework.boot.SpringApplication;

public class TestPersonalizedDataApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(PersonalizedDataApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
