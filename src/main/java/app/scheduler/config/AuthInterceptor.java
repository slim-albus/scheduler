package app.scheduler.config;

import app.scheduler.models.User;
import app.scheduler.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import app.scheduler.services.LoggerService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final LoggerService loggerService;

    private final AuthService authService;

    public AuthInterceptor(AuthService authService, LoggerService loggerService) {
        this.loggerService = loggerService;

        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        // Handle CORS preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        loggerService.info("AuthInterceptor filtering request to: " + request.getRequestURI());
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Optional<User> userOpt = authService.validateToken(token);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                request.setAttribute("user", user);
                
                String uri = request.getRequestURI();
                String role = user.getRole(); // expected: ADMIN, TEACHER, STUDENT
                
                // Admin endpoints require ADMIN role
                if (uri.startsWith("/api/admin")) {
                    if (!"ADMIN".equals(role)) {
                        // Allow TEACHER to make GET requests to specific admin endpoints for dropdowns
                        boolean isTeacherGetAllowed = "TEACHER".equals(role) && "GET".equalsIgnoreCase(request.getMethod()) && (
                                uri.startsWith("/api/admin/courses") ||
                                uri.startsWith("/api/admin/sections") ||
                                uri.startsWith("/api/admin/teachers") ||
                                uri.startsWith("/api/admin/rooms") ||
                                uri.startsWith("/api/admin/mappings")
                        );
                        
                        if (!isTeacherGetAllowed) {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("Forbidden: Admins only");
                            return false;
                        }
                    }
                }
                
                // Modifying events requires ADMIN or TEACHER role
                if (uri.startsWith("/api/schedule/event") && 
                   ("PUT".equalsIgnoreCase(request.getMethod()) || "POST".equalsIgnoreCase(request.getMethod()) || "DELETE".equalsIgnoreCase(request.getMethod()))) {
                    if ("STUDENT".equals(role)) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("Forbidden: Students cannot modify events");
                        return false;
                    }
                }
                
                return true;
            }
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Unauthorized");
        return false;
    }
}
