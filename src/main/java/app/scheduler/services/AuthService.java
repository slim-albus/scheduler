package app.scheduler.services;

import app.scheduler.models.AuthToken;
import app.scheduler.models.User;
import app.scheduler.repositories.AuthTokenRepository;
import app.scheduler.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthTokenRepository authTokenRepository;

    public AuthService(UserRepository userRepository, AuthTokenRepository authTokenRepository) {
        this.userRepository = userRepository;
        this.authTokenRepository = authTokenRepository;
    }

    public User register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        String salt = generateSalt();
        String hash = hashPassword(password, salt);

        User user = new User(username, hash, salt, "USER");
        return userRepository.save(user);
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        String hash = hashPassword(password, user.getSalt());

        if (!hash.equals(user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = UUID.randomUUID().toString();
        AuthToken authToken = new AuthToken(token, user, LocalDateTime.now().plusDays(1));
        authTokenRepository.save(authToken);

        return token;
    }

    public Optional<User> validateToken(String token) {
        Optional<AuthToken> authTokenOpt = authTokenRepository.findByToken(token);
        if (authTokenOpt.isPresent()) {
            AuthToken authToken = authTokenOpt.get();
            if (authToken.getExpiresAt().isAfter(LocalDateTime.now())) {
                return Optional.of(authToken.getUser());
            }
        }
        return Optional.empty();
    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Base64.getDecoder().decode(salt));
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
