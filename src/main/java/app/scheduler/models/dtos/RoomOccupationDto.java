package app.scheduler.models.dtos;

public class RoomOccupationDto {
    public String roomId;
    public String roomName;
    public String building;
    public int level;
    public boolean isOccupied;
    public EventDto currentEvent;
    
    public RoomOccupationDto() {}
}
