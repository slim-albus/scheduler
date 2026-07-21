package app.scheduler.generator;

import app.scheduler.models.*;
import app.scheduler.utils.BitmaskUtils;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class GeneticAlgorithmGenerator implements ScheduleGenerator {

    private static final int MAX_SEARCH_NODES = 50_000;
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
    public String getAlgorithmName() {
        return "Constraint Backtracking & Randomized Greedy";
    }

    @Override
    public boolean supportsPartialGeneration() {
        return true;
    }

    @Override
    public List<Event> generate(GeneratorInput input) {
        searchNodes = 0;
        searchLimitHit = false;

        List<ClassInstance> instances = buildClassInstances(input);
        List<ScheduleItem> placed = randomizedGreedyPlacement(instances, input);

        if (placed.size() != instances.size()) {
            placed = new ArrayList<>();
            if (!placeAll(instances, placed, input)) {
                List<ScheduleItem> greedyPlaced = greedyPlacement(instances, input);
                if (greedyPlaced.size() > placed.size()) {
                    placed = greedyPlaced;
                }
            }
        }

        // Map final ScheduleItems to Event objects
        List<Event> events = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (ScheduleItem item : placed) {
            Event event = new Event();
            event.setId(item.id != null ? item.id : UUID.randomUUID().toString());
            event.setType(item.kind);
            event.setTopic(getCourseName(input, item.courseId) + " " + item.kind);
            event.setSectionId(item.sectionId);
            event.setCourseId(item.courseId);
            event.setTeacherId(item.teacherId);
            event.setRoomId(item.roomId);
            event.setSemesterId(input.getSemester().getId());
            event.setBatchId(getBatchIdForSection(input, item.sectionId));
            event.setLabGroup(item.labGroup != null ? item.labGroup : 0);
            event.setDay(item.day + 1);
            event.setPeriod(item.period + 1);
            event.setWeek(1); // Standardized to week 1 as default
            event.setStartDateTime(now);
            event.setEndDateTime(now.plusMinutes(90));
            event.setDurationMinutes(90);
            event.setInstance(0);
            event.setStatus("SCHEDULED");
            event.setCreatedAt(now);
            event.setVersion(1);
            events.add(event);
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
                // HiLCoE theory session count: course.weeklyTheoryCount() (or default to 2 theory classes per week)
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

    private List<ScheduleItem> randomizedGreedyPlacement(List<ClassInstance> instances, GeneratorInput input) {
        List<ScheduleItem> best = List.of();
        for (int attempt = 0; attempt < 800; attempt++) {
            Random random = new Random(attempt);
            List<ClassInstance> remaining = new ArrayList<>(instances);
            List<ScheduleItem> placed = new ArrayList<>();
            while (!remaining.isEmpty()) {
                int smallestDomain = Integer.MAX_VALUE;
                List<ClassInstance> tied = new ArrayList<>();
                for (ClassInstance instance : remaining) {
                    int size = validCandidates(instance, placed, input).size();
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
                List<Candidate> candidates = validCandidates(next, placed, input).stream()
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

    private List<ScheduleItem> greedyPlacement(List<ClassInstance> instances, GeneratorInput input) {
        List<ClassInstance> remaining = new ArrayList<>(instances);
        List<ScheduleItem> placed = new ArrayList<>();
        while (!remaining.isEmpty()) {
            ClassInstance next = remaining.stream()
                    .min(Comparator.comparingInt(instance -> validCandidates(instance, placed, input).size()))
                    .orElseThrow();
            List<Candidate> candidates = validCandidates(next, placed, input);
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

    private boolean placeAll(List<ClassInstance> remaining, List<ScheduleItem> placed, GeneratorInput input) {
        if (++searchNodes > MAX_SEARCH_NODES) {
            searchLimitHit = true;
            return false;
        }
        if (remaining.isEmpty()) {
            return true;
        }

        ClassInstance next = remaining.stream()
                .min(Comparator.comparingInt(instance -> validCandidates(instance, placed, input).size()))
                .orElseThrow();
        List<Candidate> candidates = validCandidates(next, placed, input).stream()
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
            if (placeAll(nextRemaining, placed, input)) {
                return true;
            }
            placed.remove(placed.size() - 1);
        }
        return false;
    }

    private List<Candidate> validCandidates(ClassInstance instance, List<ScheduleItem> placed, GeneratorInput input) {
        List<Candidate> candidates = new ArrayList<>();
        Teacher teacher = input.getTeachers().stream()
                .filter(t -> t.getId().equals(instance.teacherId))
                .findFirst()
                .orElse(null);
        if (teacher == null) return candidates;

        for (int day : TEACHING_DAYS) {
            for (int period : PERIODS) {
                if (!BitmaskUtils.isAvailable(teacher.getAvailabilityBitmask(), day, period)) {
                    continue;
                }
                if (dailyLoad(placed, instance, day) >= 3) {
                    continue;
                }
                if (hasSameTheoryCourseOnDay(placed, instance, day)) {
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
                    if (!BitmaskUtils.isAvailable(room.getAvailabilityBitmask(), day, period)) {
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
            return "COMPUTER_LAB".equals(room.getType());
        }
        return "LECTURE_ROOM".equals(room.getType());
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

