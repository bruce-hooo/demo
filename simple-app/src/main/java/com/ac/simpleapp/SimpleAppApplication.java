package com.ac.simpleapp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@MapperScan("com.ac.simpleapp.*")
@SpringBootApplication
@RestController
public class SimpleAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimpleAppApplication.class, args);
	}

	@PostMapping("/test")
	public String test(){
		return "test";
	}
}
