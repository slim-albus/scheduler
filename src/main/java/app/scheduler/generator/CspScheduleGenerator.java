package app.scheduler.generator;

import app.scheduler.models.*;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;
import app.scheduler.services.LoggerService;

@Component
public class CspScheduleGenerator implements ScheduleGenerator {

    private final LoggerService log;

    public CspScheduleGenerator(LoggerService log) {
        this.log = log;
    }

    private static final int MAX_SEARCH_NODES = 500_000;
    private int searchNodes;
    private boolean searchLimitHit;

    // We'll define helper records/classes matching HiLCoE structure
    private static class ClassInstance {
        String id;
        String offeringId; // mapped from BatchCourseMapping.id
        String courseId;
        String sectionId;
        Integer labGroup; // null for Theory, 1 or 2 for Lab
        String teacherId;
        int size;
        boolean needsTv;
        String kind; // "THEORY" or "LAB"

        ClassInstance(String id, String offeringId, String courseId, String kind, String sectionId,
                      Integer labGroup, String teacherId, int size, boolean needsTv) {
            this.id = id;
            this.offeringId = offeringId;
            this.courseId = courseId;
            this.kind = kind;
            this.sectionId = sectionId;
            this.labGroup = labGroup;
            this.teacherId = teacherId;
            this.size = size;
            this.needsTv = needsTv;
        }
    }

    private static class Candidate {
        int day; // 0 to 5 (or 1 to 6)
        int period; // 0 to 4 (or 1 to 5)
        Room room;
        int score;
        List<String> warnings;

        Candidate(int day, int period, Room room, int score, List<String> warnings) {
            this.day = day;
            this.period = period;
            this.room = room;
            this.score = score;
            this.warnings = warnings;
        }
    }

    private static class ScheduleItem {
        String id;
        String offeringId;
        String courseId;
        String sectionId;
        Integer labGroup;
        String roomId;
        String teacherId;
        int day;
        int period;
        String kind;
        List<String> warnings;

        ScheduleItem(String id, String offeringId, String courseId, String sectionId, Integer labGroup,
                     String roomId, String teacherId, int day, int period, String kind, List<String> warnings) {
            this.id = id;
            this.offeringId = offeringId;
            this.courseId = courseId;
            this.sectionId = sectionId;
            this.labGroup = labGroup;
            this.roomId = roomId;
            this.teacherId = teacherId;
            this.day = day;
            this.period = period;
            this.kind = kind;
            this.warnings = warnings;
        }
    }

    private static final List<Integer> TEACHING_DAYS = List.of(0, 1, 2, 3, 4, 5); // 0-indexed days (Mon-Sat)
    private static final List<Integer> PERIODS = List.of(0, 1, 2, 3, 4); // 5 periods

    @Override
    public boolean supportsPartialGeneration() {
        return true;
    }

    @Override
    public List<Event> generate(GeneratorInput input) {
        List<ClassInstance> instances = buildClassInstances(input);
        log.logSchedule(String.format("Starting CSP Schedule Generation for %d total class instances", instances.size()));
        List<ScheduleItem> placed = new ArrayList<>();

        boolean success = false;
        // Progressive relaxation level loop:
        // Level 0: Strict (daily load = 3, no same theory course on same day)
        // Level 1: Relaxed load (daily load = 5, no same theory course on same day)
        // Level 2: Relaxed load and relaxed course distribution (daily load = 5, allow same theory course)
        for (int level = 0; level <= 2; level++) {
            log.logSchedule(String.format("Attempting scheduling at Relaxation Level %d...", level));
            searchNodes = 0;
            searchLimitHit = false;

            // 1. Try randomized greedy placement
            placed = randomizedGreedyPlacement(instances, input, level);
            if (placed.size() == instances.size()) {
                log.logSchedule(String.format("Successfully generated schedule using Randomized Greedy Placement at Level %d", level));
                success = true;
                break;
            }

            // 2. Try backtracking search with forward checking
            placed = new ArrayList<>();
            log.logSchedule(String.format("Randomized greedy failed at Level %d. Falling back to Backtracking CSP search...", level));
            if (placeAll(instances, placed, input, level)) {
                log.logSchedule(String.format("Successfully generated schedule using Backtracking CSP Search at Level %d. Search nodes explored: %d", level, searchNodes));
                success = true;
                break;
            }
            if (searchLimitHit) {
                log.logSchedule(String.format("WARNING: Search node limit (%d) hit at Level %d. Branch pruning was insufficient.", MAX_SEARCH_NODES, level));
            }
        }

        // 3. Fallback to greedy if still not completely successful
        if (!success) {
            log.logSchedule("WARNING: CSP Search failed at all levels. Falling back to best-effort Greedy Placement.");
            List<ScheduleItem> greedyPlaced = greedyPlacement(instances, input, 2);
            if (greedyPlaced.size() > placed.size()) {
                placed = greedyPlaced;
            }
        }

        // Map final ScheduleItems to Event objects for all weeks of the semester
        List<Event> events = new ArrayList<>();
        int totalWeeks = input.getSemester().getWeeks() > 0 ? input.getSemester().getWeeks() : 16;
        java.time.LocalDate semStart = input.getSemester().getStartDate() != null ? input.getSemester().getStartDate() : java.time.LocalDate.now();
        
        // Ensure semStart is a Monday
        while (semStart.getDayOfWeek() != DayOfWeek.MONDAY) {
            semStart = semStart.minusDays(1);
        }

        for (int week = 1; week <= totalWeeks; week++) {
            for (ScheduleItem item : placed) {
                Event event = new Event();
                event.setId(UUID.randomUUID().toString()); // new ID for each week instance

                event.setType(item.kind.equals("THEORY") ? "LECTURE" : "LAB");
                event.setTopic(getCourseName(input, item.courseId) + " " + (item.kind.equals("THEORY") ? "Lecture" : "Lab (Group " + (item.labGroup != null ? item.labGroup : 0) + ")"));
                event.setSectionId(item.sectionId);
                event.setCourseId(item.courseId);
                event.setTeacherId(item.teacherId);
                event.setRoomId(item.roomId);
                event.setSemesterId(input.getSemester().getId());
                event.setBatchId(getBatchIdForSection(input, item.sectionId));
                event.setLabGroup(item.labGroup != null ? item.labGroup : 0);
                event.setDay(item.day + 1);
                event.setPeriod(item.period + 1);
                event.setWeek(week);
                // Calculate correct date for this event
                // day=0 is Monday, day=5 is Saturday
                java.time.LocalDate eventDate = semStart.plusWeeks(week - 1).plusDays(item.day);
                event.setDate(eventDate);
                event.setStatus("SCHEDULED");
                event.setVersion(1);
                events.add(event);
            }
        }

        return events;
    }

    private String getCourseName(GeneratorInput input, String courseId) {
        return input.getCourses().stream()
                .filter(c -> c.getId().equals(courseId))
                .map(Course::getName)
                .findFirst()
                .orElse("Course");
    }

    private String getBatchIdForSection(GeneratorInput input, String sectionId) {
        return input.getSections().stream()
                .filter(s -> s.getId().equals(sectionId))
                .map(Section::getBatchId)
                .findFirst()
                .orElse("");
    }

    private List<ClassInstance> buildClassInstances(GeneratorInput input) {
        List<ClassInstance> instances = new ArrayList<>();
        for (BatchCourseMapping mapping : input.getBatchCourseMappings()) {
            if (mapping.getLectureTeacherId() == null) {
                continue;
            }
            Course course = input.getCourses().stream()
                    .filter(c -> c.getId().equals(mapping.getCourseId()))
                    .findFirst()
                    .orElse(null);
            if (course == null) continue;

            List<Section> batchSections = input.getSections().stream()
                    .filter(s -> s.getBatchId().equals(mapping.getBatchId()))
                    .sorted(Comparator.comparing(Section::getName))
                    .toList();

            for (Section section : batchSections) {
                int theoryCount = 2; // default
                for (int i = 0; i < theoryCount; i++) {
                    instances.add(new ClassInstance(UUID.randomUUID().toString(), mapping.getId(), course.getId(),
                            "THEORY", section.getId(), null, mapping.getLectureTeacherId(), section.getStudentCount(), course.isHasLab()));
                }

                if (course.isHasLab() && mapping.getLabInstructorId() != null) {
                    // Split sections into 2 lab groups (G1 and G2) as modeled in HiLCoE structure
                    int halfSize = (int) Math.ceil(section.getStudentCount() / 2.0);
                    int remainingSize = section.getStudentCount() - halfSize;

                    // Lab group 1
                    instances.add(new ClassInstance(UUID.randomUUID().toString(), mapping.getId(), course.getId(),
                            "LAB", section.getId(), 1, mapping.getLabInstructorId(), halfSize, false));
                    // Lab group 2
                    instances.add(new ClassInstance(UUID.randomUUID().toString(), mapping.getId(), course.getId(),
                            "LAB", section.getId(), 2, mapping.getLabInstructorId(), remainingSize, false));
                }
            }
        }
        return instances;
    }

    // Tries multiple randomized greedy placements to find the best possible schedule.
    // This provides a good initial layout for the solver or serves as a fallback.
    private List<ScheduleItem> randomizedGreedyPlacement(List<ClassInstance> instances, GeneratorInput input, int relaxationLevel) {
        List<ScheduleItem> best = List.of();
        for (int attempt = 0; attempt < 800; attempt++) {
            Random random = new Random(attempt);
            List<ClassInstance> remaining = new ArrayList<>(instances);
            List<ScheduleItem> placed = new ArrayList<>();
            while (!remaining.isEmpty()) {
                // Find the class with the smallest number of valid slots (Most Constrained Variable)
                int smallestDomain = Integer.MAX_VALUE;
                List<ClassInstance> tied = new ArrayList<>();
                for (ClassInstance instance : remaining) {
                    int size = validCandidates(instance, placed, input, relaxationLevel).size();
                    if (size < smallestDomain) {
                        smallestDomain = size;
                        tied.clear();
                        tied.add(instance);
                    } else if (size == smallestDomain) {
                        tied.add(instance);
                    }
                }
                if (smallestDomain == 0) {
                    break;
                }
                ClassInstance next = tied.get(random.nextInt(tied.size()));
                List<Candidate> candidates = validCandidates(next, placed, input, relaxationLevel).stream()
                        .sorted(Comparator.comparingInt((Candidate c) -> c.score).reversed())
                        .toList();
                int choiceLimit = Math.min(5, candidates.size());
                Candidate selected = candidates.get(random.nextInt(choiceLimit));
                placed.add(new ScheduleItem(UUID.randomUUID().toString(), next.offeringId, next.courseId, next.sectionId, next.labGroup,
                        selected.room.getId(), next.teacherId, selected.day, selected.period, next.kind, selected.warnings));
                remaining.remove(next);
            }
            if (placed.size() > best.size()) {
                best = placed;
            }
            if (placed.size() == instances.size()) {
                return placed;
            }
        }
        return best;
    }

    private List<ScheduleItem> greedyPlacement(List<ClassInstance> instances, GeneratorInput input, int relaxationLevel) {
        List<ClassInstance> remaining = new ArrayList<>(instances);
        List<ScheduleItem> placed = new ArrayList<>();
        while (!remaining.isEmpty()) {
            ClassInstance next = remaining.stream()
                    .min(Comparator.comparingInt(instance -> validCandidates(instance, placed, input, relaxationLevel).size()))
                    .orElseThrow();
            List<Candidate> candidates = validCandidates(next, placed, input, relaxationLevel);
            if (candidates.isEmpty()) {
                return placed;
            }
            Candidate best = candidates.stream()
                    .max(Comparator.comparingInt(c -> c.score))
                    .orElseThrow();
            placed.add(new ScheduleItem(UUID.randomUUID().toString(), next.offeringId, next.courseId, next.sectionId, next.labGroup,
                    best.room.getId(), next.teacherId, best.day, best.period, next.kind, best.warnings));
            remaining.remove(next);
        }
        return placed;
    }

    // Recursively tries to place all classes using backtracking.
    // If it hits a dead end, it backtracks and tries a different slot.
    private boolean placeAll(List<ClassInstance> remaining, List<ScheduleItem> placed, GeneratorInput input, int relaxationLevel) {
        // Prevent infinite loops / excessive time taken by setting a search node limit
        if (++searchNodes > MAX_SEARCH_NODES) {
            searchLimitHit = true;
            return false;
        }
        if (searchNodes % 50000 == 0) {
            log.logSchedule(String.format("Backtracking search in progress... Explored %d nodes. Placed %d/%d items.", searchNodes, placed.size(), placed.size() + remaining.size()));
        }
        if (remaining.isEmpty()) {
            return true;
        }

        ClassInstance next = remaining.stream()
                .min(Comparator.comparingInt(instance -> validCandidates(instance, placed, input, relaxationLevel).size()))
                .orElseThrow();
        List<Candidate> candidates = validCandidates(next, placed, input, relaxationLevel).stream()
                .sorted(Comparator.comparingInt((Candidate c) -> c.score).reversed())
                .toList();
        if (candidates.isEmpty()) {
            return false;
        }

        List<ClassInstance> nextRemaining = new ArrayList<>(remaining);
        nextRemaining.remove(next);
        for (Candidate candidate : candidates) {
            ScheduleItem item = new ScheduleItem(UUID.randomUUID().toString(), next.offeringId, next.courseId, next.sectionId, next.labGroup,
                    candidate.room.getId(), next.teacherId, candidate.day, candidate.period, next.kind, candidate.warnings);
            placed.add(item);
            
            // Forward Checking: check if any remaining variable now has domain size 0
            boolean forwardCheckingFailed = false;
            for (ClassInstance rem : nextRemaining) {
                if (validCandidates(rem, placed, input, relaxationLevel).isEmpty()) {
                    forwardCheckingFailed = true;
                    break;
                }
            }
            
            if (!forwardCheckingFailed) {
                if (placeAll(nextRemaining, placed, input, relaxationLevel)) {
                    return true;
                }
            }
            // Backtrack: undo the assignment
            placed.remove(placed.size() - 1);
        }
        return false;
    }

    // Evaluates how good a candidate slot is based on soft constraints.
    // Returns a higher score for better slots.
    private int evaluateCandidate(ClassInstance instance, Candidate candidate, List<ScheduleItem> placed, GeneratorInput input, int relaxationLevel) {
        int score = 100;

        // Try to place the same section in the same room consecutively
        boolean sameSectionSameRoom = placed.stream()
                .anyMatch(p -> p.sectionId.equals(instance.sectionId) && p.day == candidate.day &&
                        (p.period == candidate.period - 1 || p.period == candidate.period + 1) &&
                        p.roomId.equals(candidate.room.getId()));
        if (sameSectionSameRoom) score += 50;

        return score;
    }

    private List<Candidate> validCandidates(ClassInstance instance, List<ScheduleItem> placed, GeneratorInput input, int relaxationLevel) {
        List<Candidate> candidates = new ArrayList<>();
        Teacher teacher = input.getTeachers().stream()
                .filter(t -> t.getId().equals(instance.teacherId))
                .findFirst()
                .orElse(null);
        if (teacher == null) return candidates;

        int maxDailyLoad = (relaxationLevel >= 1) ? 5 : 3;
        boolean enforceSameDayTheory = (relaxationLevel == 0);

        for (int day : TEACHING_DAYS) {
            for (int period : PERIODS) {
                if (dailyLoad(placed, instance, day) >= maxDailyLoad) {
                    continue;
                }
                if (enforceSameDayTheory && hasSameTheoryCourseOnDay(placed, instance, day)) {
                    continue;
                }
                if (placed.stream().anyMatch(item -> item.day == day && item.period == period
                        && (item.teacherId.equals(instance.teacherId) || sameEntity(item, instance)))) {
                    continue;
                }

                for (Room room : input.getRooms()) {
                    if (!roomFits(room, instance)) {
                        continue;
                    }
                    if (placed.stream().anyMatch(item -> item.day == day && item.period == period && item.roomId.equals(room.getId()))) {
                        continue;
                    }
                    Candidate candidate = score(day, period, room, instance, placed, input);
                    candidates.add(candidate);
                }
            }
        }
        return candidates;
    }

    private boolean sameEntity(ScheduleItem item, ClassInstance instance) {
        if ("THEORY".equals(instance.kind)) {
            return Objects.equals(instance.sectionId, item.sectionId);
        }
        if ("THEORY".equals(item.kind)) {
            return Objects.equals(instance.sectionId, item.sectionId);
        }
        return Objects.equals(instance.sectionId, item.sectionId) && Objects.equals(instance.labGroup, item.labGroup);
    }

    private int dailyLoad(List<ScheduleItem> items, ClassInstance instance, int day) {
        if ("LAB".equals(instance.kind)) {
            return (int) items.stream()
                    .filter(item -> item.day == day)
                    .filter(item -> ("THEORY".equals(item.kind) && Objects.equals(item.sectionId, instance.sectionId))
                            || ("LAB".equals(item.kind) && Objects.equals(item.sectionId, instance.sectionId) && Objects.equals(item.labGroup, instance.labGroup)))
                    .count();
        }

        int theoryLoad = (int) items.stream()
                .filter(item -> item.day == day)
                .filter(item -> "THEORY".equals(item.kind))
                .filter(item -> Objects.equals(item.sectionId, instance.sectionId))
                .count();

        Set<Integer> labGroups = new HashSet<>();
        for (ScheduleItem item : items) {
            if (item.labGroup != null && Objects.equals(item.sectionId, instance.sectionId)) {
                labGroups.add(item.labGroup);
            }
        }
        if (instance.labGroup != null) {
            labGroups.add(instance.labGroup);
        }
        if (labGroups.isEmpty()) {
            return theoryLoad;
        }

        int maxLoad = theoryLoad;
        for (int labGroup : labGroups) {
            int labLoad = (int) items.stream()
                    .filter(item -> item.day == day)
                    .filter(item -> "LAB".equals(item.kind))
                    .filter(item -> Objects.equals(item.sectionId, instance.sectionId) && Objects.equals(item.labGroup, labGroup))
                    .count();
            maxLoad = Math.max(maxLoad, theoryLoad + labLoad);
        }
        return maxLoad;
    }

    private boolean hasSameTheoryCourseOnDay(List<ScheduleItem> placed, ClassInstance instance, int day) {
        if (!"THEORY".equals(instance.kind)) {
            return false;
        }
        return placed.stream()
                .filter(item -> item.day == day)
                .filter(item -> "THEORY".equals(item.kind))
                .anyMatch(item -> item.courseId.equals(instance.courseId)
                        && item.sectionId.equals(instance.sectionId));
    }

    private boolean roomFits(Room room, ClassInstance instance) {
        if (room.getCapacity() < instance.size) {
            return false;
        }
        if ("LAB".equals(instance.kind)) {
            return "LAB".equals(room.getType());
        }
        return "LECTURE".equals(room.getType());
    }

    private Candidate score(int day, int period, Room room, ClassInstance instance, List<ScheduleItem> placed, GeneratorInput input) {
        int score = 100;
        List<String> warnings = new ArrayList<>();

        // Preference scores
        if (period < 3) { // Morning slots preferred
            score += 5;
        }

        // Back to back student session check
        boolean backToBack = placed.stream()
                .filter(item -> item.day == day)
                .filter(item -> sameEntity(item, instance))
                .anyMatch(item -> Math.abs(item.period - period) == 1);
        if (backToBack) {
            score -= 25;
        }

        return new Candidate(day, period, room, score, warnings);
    }
}
