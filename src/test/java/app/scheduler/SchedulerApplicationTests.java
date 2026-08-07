package app.scheduler;

import app.scheduler.generator.GeneratorConfig;
import app.scheduler.models.Event;
import app.scheduler.models.Semester;
import app.scheduler.services.ScheduleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SchedulerApplicationTests {

    @Autowired
    private ScheduleService scheduleService;

    @Test
    void testScheduleGenerationAndConflictCheck() {
        Semester activeSemester = scheduleService.getActiveSemester();
        assertNotNull(activeSemester, "Active semester should not be null after seeding");
        
        System.out.println("=== Active Semester: " + activeSemester.getName() + " ===");
        
        List<Event> events = scheduleService.generateSchedule(activeSemester.getId(), new GeneratorConfig());
        assertNotNull(events);
        assertFalse(events.isEmpty(), "Generated events should not be empty");
        
        System.out.println("Total events generated: " + events.size());
        
        int conflicts = 0;
        for (int i = 0; i < events.size(); i++) {
            Event e1 = events.get(i);
            if ("CANCELED".equals(e1.getStatus())) continue;
            
            for (int j = i + 1; j < events.size(); j++) {
                Event e2 = events.get(j);
                if ("CANCELED".equals(e2.getStatus())) continue;
                
                // Same slot check
                if (e1.getWeek() == e2.getWeek() && e1.getDay() == e2.getDay() && e1.getPeriod() == e2.getPeriod()) {
                    // 1. Same Room
                    if (e1.getRoomId().equals(e2.getRoomId())) {
                        System.err.printf("CONFLICT: Same Room %s at Week %d, Day %d, Period %d (Topic 1: %s, Topic 2: %s)\n",
                                e1.getRoomId(), e1.getWeek(), e1.getDay(), e1.getPeriod(), e1.getTopic(), e2.getTopic());
                        conflicts++;
                    }
                    // 2. Same Teacher
                    if (e1.getTeacherId().equals(e2.getTeacherId())) {
                        System.err.printf("CONFLICT: Same Teacher %s at Week %d, Day %d, Period %d (Topic 1: %s, Topic 2: %s)\n",
                                e1.getTeacherId(), e1.getWeek(), e1.getDay(), e1.getPeriod(), e1.getTopic(), e2.getTopic());
                        conflicts++;
                    }
                    // 3. Same Section / Student Group collision
                    if (e1.getSectionId().equals(e2.getSectionId())) {
                        if (e1.getLabGroup() == 0 || e2.getLabGroup() == 0 || e1.getLabGroup() == e2.getLabGroup()) {
                            System.err.printf("CONFLICT: Same Section %s (LabGroup 1: %d, LabGroup 2: %d) at Week %d, Day %d, Period %d (Topic 1: %s, Topic 2: %s)\n",
                                    e1.getSectionId(), e1.getLabGroup(), e2.getLabGroup(), e1.getWeek(), e1.getDay(), e1.getPeriod(), e1.getTopic(), e2.getTopic());
                            conflicts++;
                        }
                    }
                }
            }
        }
        
        System.out.println("=== Total conflicts detected: " + conflicts + " ===");
        assertEquals(0, conflicts, "Schedule should be generated with zero conflicts!");
    }
}
