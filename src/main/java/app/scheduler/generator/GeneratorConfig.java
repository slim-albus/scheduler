package app.scheduler.generator;

public class GeneratorConfig {
    private int populationSize = 200;
    private int generations = 1000;
    private double mutationRate = 0.15;
    private double crossoverRate = 0.85;
    private double elitismRate = 0.10;
    private double optimalFitnessThreshold = 0.95;
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

    // Getters and Setters omitted for brevity but they should be here
    public int getPopulationSize() { return populationSize; }
    public void setPopulationSize(int populationSize) { this.populationSize = populationSize; }
}
