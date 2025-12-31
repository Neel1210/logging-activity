package me.demo.logging_Activity;

import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class LoggingActivityApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoggingActivityApplication.class, args);
	}

}
