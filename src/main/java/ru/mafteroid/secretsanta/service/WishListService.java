package ru.mafteroid.secretsanta.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mafteroid.secretsanta.dto.CreateEventRequest;
import ru.mafteroid.secretsanta.dto.CreateWishListItemRequest;
import ru.mafteroid.secretsanta.dto.WishlistItemResponse;
import ru.mafteroid.secretsanta.entity.User;
import ru.mafteroid.secretsanta.entity.WishListItem;
import ru.mafteroid.secretsanta.repository.UserRepository;
import ru.mafteroid.secretsanta.repository.WishListItemRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WishListService {
    private final WishListItemRepository wishListItemRepository;
    private final UserRepository userRepository;
    public WishListService(WishListItemRepository wishListItemRepository, UserRepository userRepository) {
        this.wishListItemRepository = wishListItemRepository;
        this.userRepository = userRepository;
    }

    private User findUser(String username) {
        return userRepository
                .findByUsernameIgnoreCase(username)
                .orElseThrow(() ->
                        new RuntimeException(username)
                );
    }

    private WishlistItemResponse toResponse(
            WishListItem item
    ) {
        return new WishlistItemResponse(
                item.getId(),
                item.getName(),
                item.getDescription()
        );
    }

    private String normalizeDescription(String description) {
        if (description == null) {
            return null;
        }

        String value = description.trim();

        return value.isEmpty() ? null : value;
    }

    @Transactional(readOnly = true)
    public List<WishlistItemResponse> findByUsername(String username){

        User user = findUser(username);

        return wishListItemRepository.findAllByUser_Id(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public WishlistItemResponse create(
            String username,
            CreateWishListItemRequest request
    ){
        User user = findUser(username);
        WishListItem wishListItem = new WishListItem(user, request.name(), normalizeDescription(request.description()));
        wishListItemRepository.save(wishListItem);
        return toResponse(wishListItem);
    }

    @Transactional
    public void delete(
            String username,
            UUID itemId
    ){
        User user = findUser(username);
        WishListItem wishListItem = wishListItemRepository.findById(itemId).orElseThrow(
                () -> new RuntimeException("no such item")
        );
        if (!user.getId().equals(wishListItem.getUser().getId())) {
            throw new RuntimeException("user does not belong to wishlist");
        }
        wishListItemRepository.delete(wishListItem);
    }

}
