package com.secureusermanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UserSecureManagementSystem {

	public static void main(String[] args) {
		SpringApplication.run(UserSecureManagementSystem.class, args);
	}

}
