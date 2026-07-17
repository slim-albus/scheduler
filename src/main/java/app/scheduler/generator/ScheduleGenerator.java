package app.scheduler.generator;

import app.scheduler.models.Event;
import java.util.List;

public interface ScheduleGenerator {
    List<Event> generate(GeneratorInput input);
    String getAlgorithmName();
    boolean supportsPartialGeneration();
}
