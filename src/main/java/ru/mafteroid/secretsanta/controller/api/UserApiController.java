package ru.mafteroid.secretsanta.controller.api;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mafteroid.secretsanta.dto.UserResponse;
import ru.mafteroid.secretsanta.entity.User;
import ru.mafteroid.secretsanta.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserApiController {
    private final UserService userService;
    public UserApiController(UserService userService) {
        this.userService = userService;
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


}
