package ru.mafteroid.secretsanta.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mafteroid.secretsanta.dto.CreateEventForm;
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

    public List<Event> findEventsByUsername(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() ->
                new IllegalArgumentException("No such user: " + username)
        );
        return eventParticipantRepository.findAllByUser_Id(user.getId());
    }

    public Event findEventById(UUID id) {
        return eventRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("No such event"));
    }

    @Transactional
    public void join(String username, UUID eventId) {
        Event event = findEventById(eventId);
        //TODO: check if not participant
        User user = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() ->
                new IllegalArgumentException("No such user"));
        eventParticipantRepository.save(new EventParticipant(event, user));
    }

    @Transactional
    public Event create(CreateEventForm form, String username) {
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
        return event;
    }
}
