package app.scheduler.utils;

import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class TokenManager {
    public String generateToken(String userId, String role) {
        return UUID.randomUUID().toString();
    }
    
    public boolean validateToken(String token) {
        return token != null && !token.isEmpty();
    }
}
