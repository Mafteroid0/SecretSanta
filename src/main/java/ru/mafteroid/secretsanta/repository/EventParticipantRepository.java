package ru.mafteroid.secretsanta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.mafteroid.secretsanta.entity.Event;
import ru.mafteroid.secretsanta.entity.EventParticipant;
import ru.mafteroid.secretsanta.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, UUID> {
    @Query("""
    SELECT ep.event
    FROM EventParticipant ep
    WHERE ep.user.id = :userId
    """)
    List<Event> findAllByUser_Id(UUID userId);

    @Query("""
    SELECT ep.user
    FROM EventParticipant ep
    WHERE ep.event.id = :eventId
    """)
    List<User> findAllByEvent_Id(UUID eventId);

    List<EventParticipant> findByEvent_Id(UUID eventId);
    boolean existsByEvent_IdAndUser_Id(UUID eventId, UUID userId);
    Optional<EventParticipant> findByEvent_IdAndUser_Id(UUID eventId, UUID userId);

    void deleteByEvent_IdAndUser_Id(UUID eventId, UUID userId);




}
