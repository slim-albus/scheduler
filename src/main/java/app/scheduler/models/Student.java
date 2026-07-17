package app.scheduler.models;

public class Student {
    private String id;
    private String name;
    private String studentId;
    private String email;
    private String sectionId;
    private String batchId;
    private int labGroup;
    private String program;
    private String year;
    private String semesterId;
    private boolean isActive;

    public Student() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSectionId() { return sectionId; }
    public void setSectionId(String sectionId) { this.sectionId = sectionId; }
    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }
    public int getLabGroup() { return labGroup; }
    public void setLabGroup(int labGroup) { this.labGroup = labGroup; }
    public String getProgram() { return program; }
    public void setProgram(String program) { this.program = program; }
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    public String getSemesterId() { return semesterId; }
    public void setSemesterId(String semesterId) { this.semesterId = semesterId; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
