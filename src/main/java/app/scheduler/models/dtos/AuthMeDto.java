package app.scheduler.models.dtos;

public class AuthMeDto {
    public String id;
    public String username;
    public String role;
    public String name;
    public String teacherId;
    
    public AuthMeDto(String id, String username, String role, String name, String teacherId) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.name = name;
        this.teacherId = teacherId;
    }
}
