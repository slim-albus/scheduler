package app.scheduler.config;

import app.scheduler.services.LoggerService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final LoggerService loggerService;

    public LoggingAspect(LoggerService loggerService) {
        this.loggerService = loggerService;
    }

    // Pointcut for all classes annotated with @RestController
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void controllerMethods() {}

    // Pointcut for all classes annotated with @Service
    @Pointcut("@within(org.springframework.stereotype.Service)")
    public void serviceMethods() {}

    // Pointcut for all classes annotated with @Repository
    @Pointcut("@within(org.springframework.stereotype.Repository)")
    public void repositoryMethods() {}

    @Before("controllerMethods()")
    public void logControllerBefore(JoinPoint joinPoint) {
        loggerService.logAction("Controller", "Executing API request: " + joinPoint.getSignature().toShortString());
    }

    @Before("serviceMethods()")
    public void logServiceBefore(JoinPoint joinPoint) {
        // Exclude LoggerService itself to prevent infinite recursion
        if (!joinPoint.getTarget().getClass().getSimpleName().equals("LoggerService")) {
            loggerService.logAction("Service", "Executing logic: " + joinPoint.getSignature().toShortString());
        }
    }

    @Before("repositoryMethods()")
    public void logRepositoryBefore(JoinPoint joinPoint) {
        loggerService.logAction("Database", "Executing query logic: " + joinPoint.getSignature().toShortString());
    }

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logControllerAfter(JoinPoint joinPoint, Object result) {
        loggerService.logAction("Controller", "Completed API request: " + joinPoint.getSignature().toShortString());
    }
}
