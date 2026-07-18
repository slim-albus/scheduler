package app.scheduler.models.dtos;

public class SlotDto {
    public int day;
    public int period;

    public SlotDto(int day, int period) {
        this.day = day;
        this.period = period;
    }
}
