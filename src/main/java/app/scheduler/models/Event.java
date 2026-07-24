package app.scheduler.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Event {
    private String id;
    private String type;
    private String topic;
    private String sectionId;
    private String courseId;
    private String teacherId;
    private String roomId;
    private String semesterId;
    private String batchId;
    private int labGroup;
    private int day;
    private int period;
    private int week;
    private LocalDate date;
    private int instance;
    private String createdBy;
    private LocalDateTime createdAt;
    private int version;
    private String status = "SCHEDULED";

    public Event() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getSectionId() { return sectionId; }
    public void setSectionId(String sectionId) { this.sectionId = sectionId; }
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getSemesterId() { return semesterId; }
    public void setSemesterId(String semesterId) { this.semesterId = semesterId; }
    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }
    public int getLabGroup() { return labGroup; }
    public void setLabGroup(int labGroup) { this.labGroup = labGroup; }
    public int getDay() { return day; }
    public void setDay(int day) { this.day = day; }
    public int getPeriod() { return period; }
    public void setPeriod(int period) { this.period = period; }
    public int getWeek() { return week; }
    public void setWeek(int week) { this.week = week; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public int getInstance() { return instance; }
    public void setInstance(int instance) { this.instance = instance; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
