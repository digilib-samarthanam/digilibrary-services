package com.samarthanam.digitallibrary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DigitallibraryApplication {

	public static void main(String[] args) {
		SpringApplication.run(DigitallibraryApplication.class, args);
		System.out.println(System.getenv("DB_PASSWORD"));
		System.out.println(System.getenv("JWT_SECRET"));
		System.out.println(System.getenv("S3_ACCESS_KEY"));
		System.out.println(System.getenv("S3_SECRET_KEY"));
	}

}
