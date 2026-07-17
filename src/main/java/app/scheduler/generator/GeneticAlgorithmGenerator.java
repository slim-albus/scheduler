package app.scheduler.generator;

import app.scheduler.models.Event;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class GeneticAlgorithmGenerator implements ScheduleGenerator {

    @Override
    public List<Event> generate(GeneratorInput input) {
        // Stub: Engine logic skipped per user request
        return Collections.emptyList();
    }

    @Override
    public String getAlgorithmName() {
        return "Genetic Algorithm";
    }

    @Override
    public boolean supportsPartialGeneration() {
        return false;
    }

    private double calculateFitness(Map<String, String> chromosome) {
        return 0.0; // Stub
    }
}
