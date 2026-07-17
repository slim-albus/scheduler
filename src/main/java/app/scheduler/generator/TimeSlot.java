package app.scheduler.generator;

public class TimeSlot {
    private int day;
    private int period;

    public TimeSlot(int day, int period) {
        this.day = day;
        this.period = period;
    }

    public int getDay() { return day; }
    public int getPeriod() { return period; }
}
