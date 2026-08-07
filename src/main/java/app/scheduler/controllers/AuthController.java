package app.scheduler.controllers;

import app.scheduler.models.Session;
import app.scheduler.models.User;
import app.scheduler.models.dtos.AuthMeDto;
import app.scheduler.models.dtos.AuthRequest;
import app.scheduler.models.dtos.AuthResponse;
import app.scheduler.services.AuthService;
import app.scheduler.services.StudentService;
import app.scheduler.services.TeacherService;
import app.scheduler.services.BatchService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final BatchService batchService;

    public AuthController(AuthService authService, 
                          StudentService studentService, TeacherService teacherService, BatchService batchService) {
        this.authService = authService;
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.batchService = batchService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        User user = authService.authenticate(request.username, request.password);
        Session session = authService.createSession(user.getId(), user.getRole());
        return ResponseEntity.ok(new AuthResponse(session.getToken()));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {
        User user = authService.register(request);
        return ResponseEntity.ok("User registered: " + user.getUsername());
    }

    @GetMapping("/me")
    public ResponseEntity<AuthMeDto> me(HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        String name = "Admin User";
        
        if ("STUDENT".equals(user.getRole()) && user.getStudentId() != null) {
            name = studentService.findById(user.getStudentId())
                .map(s -> {
                    String secName = "";
                    if (s.getSectionId() != null) {
                        secName = batchService.findSectionById(s.getSectionId())
                            .map(sec -> " - " + sec.getName())
                            .orElse("");
                    }
                    return s.getName() + secName;
                })
                .orElse(user.getUsername());
        } else if ("TEACHER".equals(user.getRole()) && user.getTeacherId() != null) {
            name = teacherService.findById(user.getTeacherId())
                .map(t -> t.getName())
                .orElse(user.getUsername());
        }
        
        return ResponseEntity.ok(new AuthMeDto(user.getId(), user.getUsername(), user.getRole(), name, user.getTeacherId()));
    }
}
