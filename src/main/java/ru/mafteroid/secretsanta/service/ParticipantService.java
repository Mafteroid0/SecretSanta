package ru.mafteroid.secretsanta.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mafteroid.secretsanta.dto.GiftAssignmentResponse;
import ru.mafteroid.secretsanta.dto.ParticipantResponse;
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

import java.util.List;
import java.util.UUID;

@Service
public class ParticipantService {
    private final EventParticipantRepository participantRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public ParticipantService(
            EventParticipantRepository participantRepository,
            EventRepository eventRepository,
            UserRepository userRepository
    ) {
        this.participantRepository = participantRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }
    @Transactional(readOnly = true)
    public List<ParticipantResponse> findAllByEventId(
            UUID eventId,
            String currentUsername
    ) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("No such event")
                );

        User currentUser =
                userRepository
                        .findByUsernameIgnoreCase(currentUsername)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("No such user")
                        );

        boolean participant =
                participantRepository.existsByEvent_IdAndUser_Id(
                        eventId,
                        currentUser.getId()
                );

        if (!participant) {
            throw new ForbiddenOperationException(
                    "User is not a participant of this event"
            );
        }

        UUID ownerId = event.getOwner().getId();

        return participantRepository
                .findByEvent_Id(eventId)
                .stream()
                .map(participantEntity ->
                        toResponse(participantEntity, ownerId)
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public GiftAssignmentResponse getMyAssignment(UUID eventId, String username) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("No such event"));
        if (!event.isStarted()){
            throw new ConflictException("Event is not started");
        }
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResourceNotFoundException("No such user"));
        EventParticipant participant = participantRepository
                .findByEvent_IdAndUser_Id(eventId, user.getId())
                .orElseThrow(() -> new ForbiddenOperationException("User is not a participant of this event."));
        User gifted = participant.getGiftedUser();
        if (gifted == null) {
            throw new BadRequestException("Gifted user is null");
        }
        return new GiftAssignmentResponse(gifted.getId(), gifted.getUsername(), gifted.getDisplayName());
    }

    private static ParticipantResponse toResponse(
            EventParticipant participant,
            UUID ownerId
    ) {
        User user = participant.getUser();

        return new ParticipantResponse(
                participant.getId(),
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getId().equals(ownerId)
        );
    }
}
