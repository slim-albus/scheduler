package app.scheduler.models;

import java.time.LocalDate;

public class Semester {
    private String id;
    private String name;
    private String code;
    private LocalDate startDate;
    private LocalDate endDate;
    private int weeks;
    private String academicYear;
    private boolean isActive;

    public Semester() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public int getWeeks() { return weeks; }
    public void setWeeks(int weeks) { this.weeks = weeks; }
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
