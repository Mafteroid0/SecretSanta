package ru.mafteroid.secretsanta.storage;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.mafteroid.secretsanta.repository.UserRepository;
import ru.mafteroid.secretsanta.repository.WishListItemRepository;

import java.util.UUID;

@Service
public class WishListImageService {private static final int MAX_IMAGE_SIZE = 1200;

    private final ImageStorage imageStorage;
    private final ImageProcessor imageProcessor;
    private final WishListItemRepository repository;
    private final UserRepository userRepository;

    public WishListImageService(
            ImageStorage imageStorage,
            ImageProcessor imageProcessor,
            WishListItemRepository repository,
            UserRepository userRepository
    ) {
        this.imageStorage = imageStorage;
        this.imageProcessor = imageProcessor;
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public void create(
            String username,
            UUID itemId,
            MultipartFile file
    ) {
        checkOwnership(username, itemId);

        byte[] image = imageProcessor.process(
                file,
                MAX_IMAGE_SIZE
        );

        imageStorage.save(
                imageKey(itemId),
                image
        );
    }

    public Resource get(UUID itemId) {
        if (!repository.existsById(itemId)) {
            throw new IllegalArgumentException(
                    "Wishlist item not found"
            );
        }

        String key = imageKey(itemId);

        return imageStorage.exists(key)
                ? imageStorage.load(key)
                : imageStorage.load("placeholders/wish.jpg");
    }

    public void delete(
            String username,
            UUID itemId
    ) {
        checkOwnership(username, itemId);

        imageStorage.delete(
                imageKey(itemId)
        );
    }

    private void checkOwnership(
            String username,
            UUID itemId
    ) {
        UUID userId = userRepository
                .findByUsernameIgnoreCase(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                )
                .getId();

        if (!repository.existsByIdAndUser_Id(
                itemId,
                userId
        )) {
            throw new IllegalArgumentException(
                    "Wishlist item does not belong to user"
            );
        }
    }

    private String imageKey(UUID itemId) {
        return "wishlist/" + itemId + ".jpg";
    }
}