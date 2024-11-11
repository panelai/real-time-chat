package swd392;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableScheduling
@SpringBootApplication
@EnableMethodSecurity
@EnableMongoRepositories
public class Swd392Application {

	public static void main(String[] args) {
		SpringApplication.run(Swd392Application.class, args);
	}

}
