package app.scheduler.exceptions;

public class ResourceNotFoundException extends SchedulerException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
