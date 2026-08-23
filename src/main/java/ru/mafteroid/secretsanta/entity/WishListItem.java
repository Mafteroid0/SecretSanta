package ru.mafteroid.secretsanta.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "wishlist_items")
public class WishListItem {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    protected WishListItem() {}

    public WishListItem(User user, String name, String description) {
        this.user = user;
        this.name = name;
        this.description = description;

    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
