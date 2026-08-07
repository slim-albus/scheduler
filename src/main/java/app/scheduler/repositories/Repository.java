package app.scheduler.repositories;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {
    T save(T entity);
    Optional<T> findById(String id);
    List<T> findAll();
    boolean delete(String id);
    boolean update(T entity);
}
