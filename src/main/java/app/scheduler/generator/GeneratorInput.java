package app.scheduler.generator;

import app.scheduler.models.*;
import java.util.List;

public class GeneratorInput {
    private Semester semester;
    private List<Batch> batches;
    private List<Section> sections;
    private List<Course> courses;
    private List<Teacher> teachers;
    private List<Room> rooms;
    private List<BatchCourseMapping> batchCourseMappings;
    private GeneratorConfig config;

    public Semester getSemester() { return semester; }
    public void setSemester(Semester semester) { this.semester = semester; }
    public List<Batch> getBatches() { return batches; }
    public void setBatches(List<Batch> batches) { this.batches = batches; }
    public List<Section> getSections() { return sections; }
    public void setSections(List<Section> sections) { this.sections = sections; }
    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) { this.courses = courses; }
    public List<Teacher> getTeachers() { return teachers; }
    public void setTeachers(List<Teacher> teachers) { this.teachers = teachers; }
    public List<Room> getRooms() { return rooms; }
    public void setRooms(List<Room> rooms) { this.rooms = rooms; }
    public List<BatchCourseMapping> getBatchCourseMappings() { return batchCourseMappings; }
    public void setBatchCourseMappings(List<BatchCourseMapping> batchCourseMappings) { this.batchCourseMappings = batchCourseMappings; }
    public GeneratorConfig getConfig() { return config; }
    public void setConfig(GeneratorConfig config) { this.config = config; }
}
