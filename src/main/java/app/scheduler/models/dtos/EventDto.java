package app.scheduler.models.dtos;

public class EventDto {
    public String id;
    public String topic;
    public int day;
    public int period;
    
    public String type;
    public String teacherName;
    public String courseName;
    public String sectionName;
    public String batchName;
    public String roomName;
    public String status;
    public int week;
    public String date;
    public int labGroup;
    
    // IDs for rescheduling and details
    public String teacherId;
    public String courseId;
    public String sectionId;
    public String batchId;
    public String roomId;
    public String semesterId;
}