package app.scheduler.repositories;

import app.scheduler.models.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends Repository<User> {
    Optional<User> findByUsername(String username);
}
