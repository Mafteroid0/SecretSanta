package ru.mafteroid.secretsanta.service;

import org.junit.jupiter.api.Test;
import ru.mafteroid.secretsanta.entity.EventParticipant;
import ru.mafteroid.secretsanta.entity.User;
import ru.mafteroid.secretsanta.exceptions.BadRequestException;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GiftAssignmentGeneratorTest {

    private final GiftAssignmentGenerator generator = new GiftAssignmentGenerator();

    @Test
    void assignsEachParticipantToAnotherUniqueUser() {
        List<User> users = List.of(user(), user(), user(), user());
        List<EventParticipant> participants = users.stream()
                .map(user -> new EventParticipant(null, user))
                .toList();

        generator.assign(participants);

        assertEquals(users.size(), new HashSet<>(participants.stream()
                .map(EventParticipant::getGiftedUser)
                .toList()).size());
        assertTrue(participants.stream()
                .noneMatch(participant -> participant.getUser() == participant.getGiftedUser()));
    }

    @Test
    void rejectsEventWithOneParticipant() {
        EventParticipant participant = new EventParticipant(null, user());

        assertThrows(BadRequestException.class,
                () -> generator.assign(List.of(participant)));
    }

    private User user() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(UUID.randomUUID());
        return user;
    }
}
