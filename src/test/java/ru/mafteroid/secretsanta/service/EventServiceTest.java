package ru.mafteroid.secretsanta.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mafteroid.secretsanta.dto.CreateEventRequest;
import ru.mafteroid.secretsanta.dto.EventResponse;
import ru.mafteroid.secretsanta.entity.Event;
import ru.mafteroid.secretsanta.entity.EventParticipant;
import ru.mafteroid.secretsanta.entity.User;
import ru.mafteroid.secretsanta.exceptions.ConflictException;
import ru.mafteroid.secretsanta.repository.EventParticipantRepository;
import ru.mafteroid.secretsanta.repository.EventRepository;
import ru.mafteroid.secretsanta.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock EventRepository eventRepository;
    @Mock UserRepository userRepository;
    @Mock EventParticipantRepository participantRepository;
    @Mock GiftAssignmentGenerator assignmentGenerator;
    @InjectMocks EventService eventService;

    @Test
    void createsEventAndAddsOwnerAsParticipant() {
        User owner = mock(User.class);
        UUID ownerId = UUID.randomUUID();
        when(owner.getId()).thenReturn(ownerId);
        when(userRepository.findByUsernameIgnoreCase("dan")).thenReturn(Optional.of(owner));
        LocalDateTime deadline = LocalDateTime.now().plusDays(1);

        EventResponse response = eventService.create(
                new CreateEventRequest("Новый год", deadline), "dan");

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        ArgumentCaptor<EventParticipant> participantCaptor =
                ArgumentCaptor.forClass(EventParticipant.class);
        verify(eventRepository).save(eventCaptor.capture());
        verify(participantRepository).save(participantCaptor.capture());

        Event savedEvent = eventCaptor.getValue();
        assertAll(
                () -> assertEquals("Новый год", response.name()),
                () -> assertEquals(ownerId, response.ownerId()),
                () -> assertEquals(deadline.atZone(ZoneId.of("Europe/Moscow")).toInstant(), response.deadline()),
                () -> assertSame(savedEvent, participantCaptor.getValue().getEvent()),
                () -> assertSame(owner, participantCaptor.getValue().getUser())
        );
    }

    @Test
    void rejectsRepeatedJoin() {
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Event event = mock(Event.class);
        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.findByUsernameIgnoreCase("dan")).thenReturn(Optional.of(user));
        when(participantRepository.existsByEvent_IdAndUser_Id(eventId, userId)).thenReturn(true);

        assertThrows(ConflictException.class, () -> eventService.join("dan", eventId));

        verify(participantRepository, never()).save(any());
    }
}
