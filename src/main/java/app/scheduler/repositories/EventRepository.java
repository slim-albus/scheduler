package app.scheduler.repositories;

import app.scheduler.models.Event;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends Repository<Event> {
    List<Event> findBySectionId(String sectionId);
    List<Event> findByTeacherId(String teacherId);
    List<Event> findByRoomId(String roomId);
    List<Event> findBySemesterId(String semesterId);
    List<Event> findBySectionIdAndWeek(String sectionId, int week);
    List<Event> findByTeacherIdAndDay(String teacherId, int day);
}
