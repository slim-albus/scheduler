package app.scheduler.utils;

import org.springframework.stereotype.Component;
import java.util.Base64;
import java.util.UUID;

@Component
public class TokenManager {
    public String generateToken(String userId, String role) {
        String header = Base64.getUrlEncoder().withoutPadding().encodeToString("{\"alg\":\"none\"}".getBytes());
        String payloadJson = String.format("{\"sub\":\"%s\",\"userId\":\"%s\",\"role\":\"%s\",\"jti\":\"%s\"}", userId, userId, role, UUID.randomUUID().toString());
        String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.getBytes());
        String signature = "mock_signature";
        return header + "." + payload + "." + signature;
    }
    
    public boolean validateToken(String token) {
        return token != null && !token.isEmpty();
    }
}
