package in.org.nnm.logging_Activity;

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
