package com.lec.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing	// Operation of the spring's default Listener
public class JpaApplication {


	public static void main(String[] args) {
		SpringApplication.run(JpaApplication.class, args);
	}


}
