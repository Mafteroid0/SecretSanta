package ru.mafteroid.secretsanta.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mafteroid.secretsanta.dto.CreateEventRequest;
import ru.mafteroid.secretsanta.dto.EventResponse;
import ru.mafteroid.secretsanta.entity.Event;
import ru.mafteroid.secretsanta.entity.EventParticipant;
import ru.mafteroid.secretsanta.entity.User;
import ru.mafteroid.secretsanta.repository.EventParticipantRepository;
import ru.mafteroid.secretsanta.repository.EventRepository;
import ru.mafteroid.secretsanta.repository.UserRepository;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventParticipantRepository eventParticipantRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository, EventParticipantRepository eventParticipantRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventParticipantRepository = eventParticipantRepository;
    }

    private static EventResponse toResponse(Event event) {
        return new EventResponse(event.getId(), event.getName(), event.getDeadline(),
                event.isStarted(), event.getOwner().getId());
    }

    public List<EventResponse> findEventsByUsername(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() ->
                new IllegalArgumentException("No such user: " + username)
        );
        return eventParticipantRepository.findAllByUser_Id(user.getId()).stream().map(EventService::toResponse)
                .toList();
    }

    public EventResponse findEventById(UUID id) {
        return toResponse(eventRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("No such event")));
    }

    @Transactional
    public void join(String username, UUID eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new IllegalArgumentException("No such event"));

        User user = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() ->
                new IllegalArgumentException("No such user"));

        if (eventParticipantRepository.existsByEvent_IdAndUser_Id(eventId, user.getId())) {
            throw new IllegalArgumentException("User already joined");
        }
        eventParticipantRepository.save(new EventParticipant(event, user));
    }

    @Transactional
    public EventResponse create(CreateEventRequest form, String username) {
        ZoneId zone = ZoneId.of("Europe/Moscow");
        Instant deadline = form.deadline()
                .atZone(zone)
                .toInstant();
        User user = userRepository.findByUsernameIgnoreCase(username).orElseThrow(
                () -> new IllegalArgumentException("No such user")
        );
        Event event = new Event(form.name(), user, deadline);
        eventRepository.save(event);
        EventParticipant eventParticipant = new EventParticipant(event, user);
        eventParticipantRepository.save(eventParticipant);
        return toResponse(event);
    }
}
