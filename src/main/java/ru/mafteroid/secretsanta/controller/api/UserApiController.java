package ru.mafteroid.secretsanta.controller.api;

import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.mafteroid.secretsanta.dto.UpdateUserRequest;
import ru.mafteroid.secretsanta.dto.UserResponse;
import ru.mafteroid.secretsanta.entity.User;
import ru.mafteroid.secretsanta.service.UserService;
import ru.mafteroid.secretsanta.storage.AvatarService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserApiController {
    private final UserService userService;
    private final AvatarService avatarService;

    public UserApiController(UserService userService, AvatarService avatarService) {
        this.userService = userService;
        this.avatarService = avatarService;
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(Authentication authentication) {
        User user = userService.getUserByUsername(
                authentication.getName()
        );

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getDisplayName()
        );
    }
    @PatchMapping("/me")
    UserResponse updateCurrentUser(Authentication authentication, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        return userService.updateUser(authentication.getName(), updateUserRequest.displayName());

    }

    @PostMapping("/me/avatar")
    public ResponseEntity<String> updateAvatar(@RequestParam("file") MultipartFile file, Authentication authentication) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }
        avatarService.update(authentication.getName(), file);
        return ResponseEntity.ok().body("Avatar updated");
    }

    @GetMapping("/{userId}/avatar")
    public ResponseEntity<Resource> getAvatar(@PathVariable("userId") UUID userId) {
        Resource resource = avatarService.get(userId);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    @DeleteMapping("/me/avatar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAvatar(Authentication authentication) {
        avatarService.delete(authentication.getName());
    }


}
