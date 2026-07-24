package app.scheduler.models.dtos;

public class RoomOccupationDto {
    public String roomId;
    public String roomName;
    public int level;
    public String roomType;
    public boolean hasEquipment;
    public boolean isOccupied;
    public EventDto currentEvent;
    
    public RoomOccupationDto() {}
}
