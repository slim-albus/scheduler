package app.scheduler.models.dtos;

import java.util.List;
import app.scheduler.models.Room;

public class SlotDto {
    public int day;
    public int period;
    public List<Room> availableRooms;
    
    public SlotDto() {}
    
    public SlotDto(int day, int period) {
        this.day = day;
        this.period = period;
    }

    public SlotDto(int day, int period, List<Room> availableRooms) {
        this.day = day;
        this.period = period;
        this.availableRooms = availableRooms;
    }
}
