package app.scheduler.repositories;

import app.scheduler.models.Session;
import java.util.List;
import java.util.Optional;

public interface SessionRepository extends Repository<Session> {
    Optional<Session> findByToken(String token);
}
