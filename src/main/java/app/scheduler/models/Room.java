package app.scheduler.models;

public class Room {
    private String id;
    private String name;
    private String type;
    private int capacity;
    private String building;
    private int level;
    private boolean hasEquipment;
    private long availabilityBitmask;
    private boolean isActive;

    public Room() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public boolean isHasEquipment() { return hasEquipment; }
    public void setHasEquipment(boolean hasEquipment) { this.hasEquipment = hasEquipment; }
    public long getAvailabilityBitmask() { return availabilityBitmask; }
    public void setAvailabilityBitmask(long availabilityBitmask) { this.availabilityBitmask = availabilityBitmask; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
