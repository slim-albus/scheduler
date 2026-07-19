package app.scheduler.models;

public class Section {
    private String id;
    private String name;
    private String batchId;
    private int studentCount;
    private boolean isActive;

    public Section() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }
    public int getStudentCount() { return studentCount; }
    public void setStudentCount(int studentCount) { this.studentCount = studentCount; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
