package app.scheduler.config;

import app.scheduler.services.LoggerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private final LoggerService loggerService;

    public RequestLoggingInterceptor(LoggerService loggerService) {
        this.loggerService = loggerService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String method = request.getMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        String path = request.getRequestURI();
        
        if (path.startsWith("/api/admin")) {
            loggerService.logAdmin(method + " " + path);
        } else if (path.startsWith("/api/schedule") || path.startsWith("/api/batch") || path.startsWith("/api/course") || path.startsWith("/api/room") || path.startsWith("/api/semester") || path.startsWith("/api/student") || path.startsWith("/api/teacher")) {
            loggerService.logSchedule(method + " " + path);
        } else if (path.startsWith("/api/auth")) {
            loggerService.logAuth(method + " " + path);
        } else if (path.startsWith("/api/")) {
            loggerService.logSystem(method + " " + path);
        }

        return true;
    }
}
