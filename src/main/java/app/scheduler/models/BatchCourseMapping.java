package app.scheduler.models;

public class BatchCourseMapping {
    private String id;
    private String batchId;
    private String courseId;
    private String lectureTeacherId;
    private String labInstructorId;
    private String semesterId;

    public BatchCourseMapping() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public String getLectureTeacherId() { return lectureTeacherId; }
    public void setLectureTeacherId(String lectureTeacherId) { this.lectureTeacherId = lectureTeacherId; }
    public String getLabInstructorId() { return labInstructorId; }
    public void setLabInstructorId(String labInstructorId) { this.labInstructorId = labInstructorId; }
    public String getSemesterId() { return semesterId; }
    public void setSemesterId(String semesterId) { this.semesterId = semesterId; }
}
