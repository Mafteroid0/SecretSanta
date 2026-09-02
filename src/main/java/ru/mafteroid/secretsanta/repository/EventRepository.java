package ru.mafteroid.secretsanta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mafteroid.secretsanta.entity.Event;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findAllByOwnerId(UUID ownerId);
    List<Event> findAllByStartedFalseAndDeadlineBefore(Instant now);
}
