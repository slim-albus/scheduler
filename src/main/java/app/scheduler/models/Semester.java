package app.scheduler.models;

import java.time.LocalDate;

public class Semester {
    private String id;
    private String name;
    private LocalDate startDate;
    private int weeks;
    private String academicYear;
    private boolean isGenerated;
    private boolean isActive;

    public Semester() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public int getWeeks() { return weeks; }
    public void setWeeks(int weeks) { this.weeks = weeks; }
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public boolean isGenerated() { return isGenerated; }
    public void setGenerated(boolean generated) { isGenerated = generated; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
