package ru.mafteroid.secretsanta.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mafteroid.secretsanta.entity.Event;
import ru.mafteroid.secretsanta.entity.User;
import ru.mafteroid.secretsanta.exceptions.ConflictException;
import ru.mafteroid.secretsanta.exceptions.ForbiddenOperationException;
import ru.mafteroid.secretsanta.repository.EventParticipantRepository;
import ru.mafteroid.secretsanta.repository.EventRepository;
import ru.mafteroid.secretsanta.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceTest {

    @Mock EventParticipantRepository participantRepository;
    @Mock EventRepository eventRepository;
    @Mock UserRepository userRepository;
    @InjectMocks ParticipantService participantService;

    @Test
    void hidesParticipantsFromOutsider() {
        UUID eventId = UUID.randomUUID();
        User user = userWithId(UUID.randomUUID());
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(mock(Event.class)));
        when(userRepository.findByUsernameIgnoreCase("dan")).thenReturn(Optional.of(user));

        assertThrows(ForbiddenOperationException.class,
                () -> participantService.findAllByEventId(eventId, "dan"));
    }

    @Test
    void rejectsAssignmentBeforeEventStarts() {
        UUID eventId = UUID.randomUUID();
        Event event = mock(Event.class);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        assertThrows(ConflictException.class,
                () -> participantService.getMyAssignment(eventId, "dan"));
    }

    @Test
    void doesNotAllowOwnerToLeave() {
        UUID eventId = UUID.randomUUID();
        User owner = mock(User.class);
        Event event = mock(Event.class);
        when(event.getOwner()).thenReturn(owner);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.findByUsernameIgnoreCase("dan")).thenReturn(Optional.of(owner));

        assertThrows(ConflictException.class,
                () -> participantService.leave(eventId, "dan"));
        verify(participantRepository, never()).deleteByEvent_IdAndUser_Id(any(), any());
    }

    private User userWithId(UUID id) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(id);
        return user;
    }
}
