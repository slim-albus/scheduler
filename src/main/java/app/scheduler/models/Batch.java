package app.scheduler.models;

public class Batch {
    private String id;
    private String name;
    private String program;
    private String year;
    private String semesterId;
    private boolean isActive;

    public Batch() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getProgram() { return program; }
    public void setProgram(String program) { this.program = program; }
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    public String getSemesterId() { return semesterId; }
    public void setSemesterId(String semesterId) { this.semesterId = semesterId; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
