package app.scheduler.config;

// import java.time.LocalDateTime;

import org.springframework.context.annotation.Configuration;
// import org.springframework.stereotype.Component;
// import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
// import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// import app.scheduler.repo.SessionRepository;
// import app.scheduler.repo.UserRepository;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class WebConfig implements WebMvcConfigurer {



//     @Component
// public class SecurityInterceptor implements HandlerInterceptor {
//     private final SessionRepository sessionRepo;
//     private final UserRepository userRepo;

//     @Override
//     public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
//         if (request.getRequestURI().startsWith("/api/auth/")) return true;

//         String authHeader = request.getHeader("Authorization");
//         if (authHeader == null || !authHeader.startsWith("Bearer ")) throw new UnauthenticatedException("Unauthorized");
//         String token = authHeader.substring(7);

//         Session session = sessionRepo.findByToken(token).orElseThrow(() -> new UnauthenticatedException("Invalid session"));
//         if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
//             sessionRepo.deleteByToken(token);
//             throw new UnauthenticatedException("Session expired");
//         }

//         User user = userRepo.findById(session.getUserId()).orElseThrow();
//         request.setAttribute("currentUser", user); // Inject session state directly into the pipeline thread
//         return true;
//     }
// }
// private final SecurityInterceptor interceptor;
//     public WebConfig(SecurityInterceptor interceptor) { this.interceptor = interceptor; }

//     @Override
//     public void addInterceptors(InterceptorRegistry registry) {
//         registry.addInterceptor(interceptor).addPathPatterns("/api/**");
//     }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
