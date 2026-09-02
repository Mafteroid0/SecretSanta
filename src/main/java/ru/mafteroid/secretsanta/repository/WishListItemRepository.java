package ru.mafteroid.secretsanta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mafteroid.secretsanta.entity.WishListItem;

import java.util.List;
import java.util.UUID;

public interface WishListItemRepository extends JpaRepository<WishListItem, UUID> {
    List<WishListItem> findAllByUser_Id(UUID userId);
    boolean existsByIdAndUser_Id(UUID id, UUID userId);
}
