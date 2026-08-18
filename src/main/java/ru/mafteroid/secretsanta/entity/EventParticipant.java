package ru.mafteroid.secretsanta.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name="event_participants")
public class EventParticipant {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gifted_user_id")
    private User giftedUser;

    protected EventParticipant() {
    }

    public EventParticipant(Event event, User user) {
        this.event = event;
        this.user = user;
        this.giftedUser = null;
    }

    public void assignGiftedUser(User giftedUser) {
        if (user == null) {
            throw new IllegalStateException("user is null");
        }
        if (this.user.getId().equals(giftedUser.getId())) {
            throw new IllegalStateException("Participant cannot be assigned to themselves");
        }
        this.giftedUser = giftedUser;
    }

    public UUID getId() {
        return id;
    }
    public Event getEvent() {
        return event;
    }

    public User getUser() {
        return user;
    }
    public User getGiftedUser() {
        return giftedUser;
    }
}
