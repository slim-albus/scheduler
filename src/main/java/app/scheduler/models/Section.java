package app.scheduler.models;

public class Section {
    private String id;
    private String name;
    private String batchId;
    private int labGroup;
    private int studentCount;
    private String semesterId;
    private boolean isActive;

    public Section() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }
    public int getLabGroup() { return labGroup; }
    public void setLabGroup(int labGroup) { this.labGroup = labGroup; }
    public int getStudentCount() { return studentCount; }
    public void setStudentCount(int studentCount) { this.studentCount = studentCount; }
    public String getSemesterId() { return semesterId; }
    public void setSemesterId(String semesterId) { this.semesterId = semesterId; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
