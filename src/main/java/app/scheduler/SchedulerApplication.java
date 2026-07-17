package app.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class SchedulerApplication {
	public static void main(String[] args) {
		SpringApplication.run(SchedulerApplication.class, args);
		System.out.println("Spring Boot server running on https://localhost:8080");
	}
}

