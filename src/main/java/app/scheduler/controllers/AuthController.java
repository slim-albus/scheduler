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
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        loggerService.logAuth("Login attempt for username: " + request.username);
        User user = authService.authenticate(request.username, request.password);
        Session session = authService.createSession(user.getId(), user.getRole());
        loggerService.logAuth("Successful login for username: " + request.username);
        return ResponseEntity.ok(new AuthResponse(session.getToken()));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {
        loggerService.logAuth("Registration attempt for username: " + request.username);
        User user = authService.register(request);
        loggerService.logAuth("Successful registration for username: " + request.username);
        return ResponseEntity.ok("User registered: " + user.getUsername());
    }
}
