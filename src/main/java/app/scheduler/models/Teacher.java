package app.scheduler.models;

public class Teacher {
    private String id;
    private String name;
    private String email;
    private String department;
    private String type;
    private long availabilityBitmask;
    private int maxClassesPerDay;
    private boolean isActive;

    public Teacher() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public long getAvailabilityBitmask() { return availabilityBitmask; }
    public void setAvailabilityBitmask(long availabilityBitmask) { this.availabilityBitmask = availabilityBitmask; }
    public int getMaxClassesPerDay() { return maxClassesPerDay; }
    public void setMaxClassesPerDay(int maxClassesPerDay) { this.maxClassesPerDay = maxClassesPerDay; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
