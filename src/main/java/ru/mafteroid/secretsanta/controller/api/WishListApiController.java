package ru.mafteroid.secretsanta.controller.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.mafteroid.secretsanta.dto.CreateWishListItemRequest;
import ru.mafteroid.secretsanta.dto.WishlistItemResponse;
import ru.mafteroid.secretsanta.service.WishListService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class WishListApiController {
    private final WishListService wishlistService;

    public WishListApiController(
            WishListService wishlistService
    ) {
        this.wishlistService = wishlistService;
    }

    @GetMapping("/users/me/wishlist")
    public List<WishlistItemResponse> getMyWishlist(
            Authentication authentication
    ) {
        return wishlistService.findByUsername(
                authentication.getName()
        );
    }

    @GetMapping("/users/{username}/wishlist")
    public List<WishlistItemResponse> getUserWishlist(
            @PathVariable String username
    ) {
        return wishlistService.findByUsername(username);
    }

    @PostMapping("/users/me/wishlist")
    @ResponseStatus(HttpStatus.CREATED)
    public WishlistItemResponse create(
            @Valid @RequestBody CreateWishListItemRequest request,
            Authentication authentication
    ) {
        return wishlistService.create(
                authentication.getName(),
                request
        );
    }

    @DeleteMapping("/users/me/wishlist/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID itemId,
            Authentication authentication
    ) {
        wishlistService.delete(
                authentication.getName(),
                itemId
        );
    }
}
