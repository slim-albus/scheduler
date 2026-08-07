package app.scheduler.models;

public class Course {
    private String id;
    private String code;
    private String name;
    private boolean hasLab;
    private int credits;
    private String department;

    public Course() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isHasLab() { return hasLab; }
    public void setHasLab(boolean hasLab) { this.hasLab = hasLab; }
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
