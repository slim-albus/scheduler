package app.scheduler.controllers;

import app.scheduler.models.Session;
import app.scheduler.models.User;
import app.scheduler.models.dtos.AuthRequest;
import app.scheduler.models.dtos.AuthResponse;
import app.scheduler.services.AuthService;
import app.scheduler.services.LoggerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final LoggerService loggerService;
    private final AuthService authService;

    public AuthController(AuthService authService, LoggerService loggerService) {
        this.loggerService = loggerService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            User user = authService.authenticate(request.username, request.password);
            Session session = authService.createSession(user.getId(), user.getRole());
            return ResponseEntity.ok(new AuthResponse(session.getToken()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        try {
            User user = authService.register(request);
            return ResponseEntity.ok("User registered: " + user.getUsername());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
