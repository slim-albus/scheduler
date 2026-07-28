package app.scheduler.generator;

public class GeneratorConfig {
    private boolean verbose = true;
    
    private int maxClassesPerDay = 3;
    private int requiredActiveDays = 5;
    private int minClassesPerDay = 2;
    private int lecturesPerCoursePerWeek = 2;
    private int labsPerCoursePerWeek = 1;
    
    private int periodsPerDay = 5;
    private int daysPerWeek = 6;
    private int startHour = 8;
    private int lunchStartHour = 13;
    private int lunchEndHour = 14;
    private int breakMinutes = 15;
    private int periodDuration = 90;

    public int getMaxClassesPerDay() { return maxClassesPerDay; }
    public void setMaxClassesPerDay(int maxClassesPerDay) { this.maxClassesPerDay = maxClassesPerDay; }
}
