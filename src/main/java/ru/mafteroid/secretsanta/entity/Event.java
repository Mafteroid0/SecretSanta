package ru.mafteroid.secretsanta.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 120)
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private Instant deadline;

    @Column(name="created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private boolean started;

    protected Event() {}
    public Event(String name, User owner, Instant deadline) {
        this.name = name;
        this.owner = owner;
        this.deadline = deadline;
        this.started = false;
    }
    public UUID getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public User getOwner() {
        return owner;
    }
    public Instant getDeadline() {
        return deadline;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public boolean isStarted() {
        return started;
    }
    public void start() {
        if (started) {
            throw new IllegalStateException("Event already started");
        }
        this.started = true;
    }

}
