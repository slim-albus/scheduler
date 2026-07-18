package app.scheduler.config;

import app.scheduler.services.LoggerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LogInterceptor implements HandlerInterceptor {
    private final LoggerService loggerService;

    public LogInterceptor(LoggerService loggerService) {
        this.loggerService = loggerService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        if (uri.startsWith("/api/admin")) {
            loggerService.logAdmin(method + " " + uri);
        } else if (uri.startsWith("/api/schedule")) {
            // we already logged specific actions in ScheduleController, but we can log base requests here if wanted
            // loggerService.logSchedule(method + " " + uri);
        } else if (uri.startsWith("/api/map")) {
            // loggerService.logMap(method + " " + uri);
        }
        return true;
    }
}
