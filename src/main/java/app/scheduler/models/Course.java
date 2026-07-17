package app.scheduler.models;

public class Course {
    private String id;
    private String code;
    private String name;
    private boolean hasLab;
    private int credits;
    private int lectureHoursPerWeek;
    private int labHoursPerWeek;
    private int midtermDuration;
    private int finalDuration;
    private String department;
    private boolean isActive;

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
    public int getLectureHoursPerWeek() { return lectureHoursPerWeek; }
    public void setLectureHoursPerWeek(int lectureHoursPerWeek) { this.lectureHoursPerWeek = lectureHoursPerWeek; }
    public int getLabHoursPerWeek() { return labHoursPerWeek; }
    public void setLabHoursPerWeek(int labHoursPerWeek) { this.labHoursPerWeek = labHoursPerWeek; }
    public int getMidtermDuration() { return midtermDuration; }
    public void setMidtermDuration(int midtermDuration) { this.midtermDuration = midtermDuration; }
    public int getFinalDuration() { return finalDuration; }
    public void setFinalDuration(int finalDuration) { this.finalDuration = finalDuration; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
