package app.scheduler.services;

import app.scheduler.models.Session;
import app.scheduler.models.User;
import app.scheduler.repositories.SessionRepository;
import app.scheduler.repositories.UserRepository;
import app.scheduler.utils.PasswordHasher;
import app.scheduler.utils.TokenManager;
import app.scheduler.services.LoggerService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import app.scheduler.exceptions.AuthenticationException;
import app.scheduler.exceptions.ValidationException;

@Service
public class AuthService {
    private final LoggerService loggerService;


    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final TokenManager tokenManager;
    private final PasswordHasher passwordHasher;

    public AuthService(UserRepository userRepository, SessionRepository sessionRepository, 
                       TokenManager tokenManager, PasswordHasher passwordHasher, LoggerService loggerService) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.tokenManager = tokenManager;
        this.passwordHasher = passwordHasher;
        this.loggerService = loggerService;
    }

    public User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

        String hash = passwordHasher.hashPassword(password, user.getSalt());

        if (!hash.equals(user.getPasswordHash())) {
            throw new AuthenticationException("Invalid credentials");
        }
        return user;
    }

    public Session createSession(String userId, String role) {
        String token = tokenManager.generateToken(userId, role);
        Session session = new Session();
        session.setToken(token);
        session.setUserId(userId);
        session.setExpiresAt(LocalDateTime.now().plusDays(1));
        return sessionRepository.save(session);
    }
    
    public User register(app.scheduler.models.dtos.AuthRequest request) {
        if (userRepository.findByUsername(request.username).isPresent()) {
            throw new ValidationException("Username already exists");
        }
        String salt = passwordHasher.generateSalt();
        String hash = passwordHasher.hashPassword(request.password, salt);
        User user = new User();
        // Fallback to username if userId is not provided to satisfy SQLite NOT NULL constraint
        user.setUserId(request.userId != null ? request.userId : request.username);
        user.setUsername(request.username);
        user.setPasswordHash(hash);
        user.setSalt(salt);
        user.setEmail(request.email);
        user.setRole(request.role != null ? request.role : "STUDENT");
        user.setActive(true);
        return userRepository.save(user);
    }

    public User autoRegister(String username, String role, String teacherId, String studentId) {
        if (userRepository.findByUsername(username).isPresent()) {
            return userRepository.findByUsername(username).get(); // Return existing if already there
        }
        String salt = passwordHasher.generateSalt();
        String hash = passwordHasher.hashPassword("password", salt);
        User user = new User();
        user.setUserId(username);
        user.setUsername(username);
        user.setPasswordHash(hash);
        user.setSalt(salt);
        user.setRole(role);
        user.setTeacherId(teacherId);
        user.setStudentId(studentId);
        user.setActive(true);
        return userRepository.save(user);
    }

    public void logout(String token) {
        sessionRepository.findByToken(token).ifPresent(s -> {
            sessionRepository.update(s);
        });
    }

    public Optional<User> validateToken(String token) {
        Optional<Session> sessionOpt = sessionRepository.findByToken(token);
        if (sessionOpt.isPresent()) {
            Session s = sessionOpt.get();
            if (s.getExpiresAt().isAfter(LocalDateTime.now())) {
                return userRepository.findById(s.getUserId());
            }
        }
        return Optional.empty();
    }
}
