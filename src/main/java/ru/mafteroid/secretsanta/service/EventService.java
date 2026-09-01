package ru.mafteroid.secretsanta.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mafteroid.secretsanta.dto.CreateEventRequest;
import ru.mafteroid.secretsanta.dto.EventResponse;
import ru.mafteroid.secretsanta.entity.Event;
import ru.mafteroid.secretsanta.entity.EventParticipant;
import ru.mafteroid.secretsanta.entity.User;
import ru.mafteroid.secretsanta.exceptions.BadRequestException;
import ru.mafteroid.secretsanta.exceptions.ConflictException;
import ru.mafteroid.secretsanta.exceptions.ForbiddenOperationException;
import ru.mafteroid.secretsanta.exceptions.ResourceNotFoundException;
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
    private final GiftAssignmentGenerator assignmentGenerator;

    public EventService(EventRepository eventRepository, UserRepository userRepository,
                        EventParticipantRepository eventParticipantRepository, GiftAssignmentGenerator assignmentGenerator) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventParticipantRepository = eventParticipantRepository;
        this.assignmentGenerator = assignmentGenerator;
    }

    private static EventResponse toResponse(Event event) {
        return new EventResponse(event.getId(), event.getName(), event.getDeadline(),
                event.isStarted(), event.getOwner().getId());
    }

    private void requireParticipant(
            Event event,
            User user
    ) {
        boolean participant =
                eventParticipantRepository
                        .existsByEvent_IdAndUser_Id(
                                event.getId(),
                                user.getId()
                        );

        if (!participant) {
            throw new ForbiddenOperationException(
                    "User is not a participant of this event"
            );
        }
    }

    private void requireOwner(
            Event event,
            User user
    ) {
        if (!event.getOwner().getId().equals(user.getId())) {
            throw new ForbiddenOperationException(
                    "Only the owner can perform this operation"
            );
        }
    }

    public List<EventResponse> findEventsByUsername(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() ->
                new ResourceNotFoundException("No such user: " + username)
        );
        return eventParticipantRepository.findAllByUser_Id(user.getId()).stream().map(EventService::toResponse)
                .toList();
    }

    public EventResponse findEventById(UUID id, String username) {
        Event event = eventRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("No such event"));
        User user = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() ->
                new ResourceNotFoundException("No such user"));

        requireParticipant(event, user);

        return toResponse(event);
    }

    @Transactional
    public void join(String username, UUID eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new ResourceNotFoundException("No such event"));

        User user = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() ->
                new ResourceNotFoundException("No such user"));

        if (eventParticipantRepository.existsByEvent_IdAndUser_Id(eventId, user.getId())) {
            throw new ConflictException("User already joined");
        }
        if (event.isStarted()){
            throw new ConflictException("Event is already started");
        }

        eventParticipantRepository.save(new EventParticipant(event, user));
    }

    @Transactional
    public EventResponse create(CreateEventRequest form, String username) {
        ZoneId zone = ZoneId.of("Europe/Moscow");
        Instant deadline = form.deadline()
                .atZone(zone)
                .toInstant();

        if (!deadline.isAfter(Instant.now())) {
            throw new BadRequestException("Deadline must be in the future");
        }
        User user = userRepository.findByUsernameIgnoreCase(username).orElseThrow(
                () -> new ResourceNotFoundException("No such user")
        );
        Event event = new Event(form.name(), user, deadline);
        eventRepository.save(event);
        EventParticipant eventParticipant = new EventParticipant(event, user);
        eventParticipantRepository.save(eventParticipant);
        return toResponse(event);
    }

    @Transactional
    public EventResponse start(UUID eventId, String username) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(()-> new ResourceNotFoundException("No such user"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("No such event"));
        requireOwner(event, user);

        assignmentGenerator.assign(eventParticipantRepository.findByEvent_Id(eventId));
        event.start();
        return toResponse(event);

    }

    @Transactional
    public void delete(UUID eventId, String username) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(()-> new ResourceNotFoundException("No such user"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("No such event"));
        requireOwner(event, user);

        eventRepository.delete(event);
    }
}
