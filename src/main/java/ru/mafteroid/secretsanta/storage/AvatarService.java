package ru.mafteroid.secretsanta.storage;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.mafteroid.secretsanta.entity.User;
import ru.mafteroid.secretsanta.service.UserService;

import java.util.UUID;

@Service
public class AvatarService {
    private final UserService userService;
    private final ImageProcessor imageProcessor;
    private final ImageStorage imageStorage;

    public AvatarService(
            UserService userService,
            ImageProcessor imageProcessor,
            ImageStorage imageStorage
    ) {
        this.userService = userService;
        this.imageProcessor = imageProcessor;
        this.imageStorage = imageStorage;
    }

    public void update(
            String username,
            MultipartFile file
    ){
        User user =
                userService.getUserByUsername(username);

        byte[] image =
                imageProcessor.process(file, 512);

        imageStorage.save(
                avatarKey(user.getId()),
                image
        );
    }
    public Resource get(UUID userId){
        String key = avatarKey(userId);

        return imageStorage.exists(key)
                ? imageStorage.load(key)
                : imageStorage.load("placeholders/avatar.jpg");
    }

    public void delete(String username) {
        User user = userService.getUserByUsername(username);

        imageStorage.delete(
                avatarKey(user.getId())
        );
    }


    private String avatarKey(UUID userId){
        return "avatars/" + userId + ".jpg";
    }
}
