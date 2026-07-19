package app.scheduler.models;

public class Batch {
    private String id;
    private String name;
    private String program;
    private String year;
    private boolean isActive;
    
    // Transient field for generating sections
    private int sectionCount;

    public Batch() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getProgram() { return program; }
    public void setProgram(String program) { this.program = program; }
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public int getSectionCount() { return sectionCount; }
    public void setSectionCount(int sectionCount) { this.sectionCount = sectionCount; }
}
