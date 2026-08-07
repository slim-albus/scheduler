package app.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class SchedulerApplication {
	public static void main(String[] args) {
		org.springframework.context.ApplicationContext ctx = SpringApplication.run(SchedulerApplication.class, args);
		app.scheduler.services.LoggerService logger = ctx.getBean(app.scheduler.services.LoggerService.class);
		logger.logSystem("Spring Boot server running on https://localhost:8080");
	}
}

