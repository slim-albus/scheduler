package app.scheduler.generator;

public class GeneratorConfig {
    private int maxClassesPerDay = 3;
    private int daysPerWeek = 6;
    private int periodsPerDay = 5;
    private int lecturesPerCoursePerWeek = 2;

    public int getMaxClassesPerDay() { return maxClassesPerDay; }
    public void setMaxClassesPerDay(int maxClassesPerDay) { this.maxClassesPerDay = maxClassesPerDay; }

    public int getDaysPerWeek() { return daysPerWeek; }
    public void setDaysPerWeek(int daysPerWeek) { this.daysPerWeek = daysPerWeek; }

    public int getPeriodsPerDay() { return periodsPerDay; }
    public void setPeriodsPerDay(int periodsPerDay) { this.periodsPerDay = periodsPerDay; }

    public int getLecturesPerCoursePerWeek() { return lecturesPerCoursePerWeek; }
    public void setLecturesPerCoursePerWeek(int lecturesPerCoursePerWeek) { this.lecturesPerCoursePerWeek = lecturesPerCoursePerWeek; }
}
