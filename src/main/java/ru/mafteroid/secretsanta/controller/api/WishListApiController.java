package ru.mafteroid.secretsanta.controller.api;

import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.mafteroid.secretsanta.dto.CreateWishListItemRequest;
import ru.mafteroid.secretsanta.dto.ImportWishlistRequest;
import ru.mafteroid.secretsanta.dto.WishlistItemResponse;
import ru.mafteroid.secretsanta.service.WishListImportService;
import ru.mafteroid.secretsanta.service.WishListService;
import ru.mafteroid.secretsanta.storage.WishListImageService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class WishListApiController {
    private final WishListService wishlistService;
    private final WishListImportService wishListImportService;
    private final WishListImageService wishListImageService;

    public WishListApiController(
            WishListService wishlistService,
            WishListImportService wishListImportService,
            WishListImageService wishListImageService) {
        this.wishlistService = wishlistService;
        this.wishListImportService = wishListImportService;
        this.wishListImageService = wishListImageService;

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
        wishListImageService.delete(
                authentication.getName(),
                itemId
        );
        wishlistService.delete(
                authentication.getName(),
                itemId
        );
    }

    @PostMapping("/users/me/wishlist/import")
    @ResponseStatus(HttpStatus.CREATED)
    public List<WishlistItemResponse> importWishlist(
            @Valid @RequestBody ImportWishlistRequest request,
            Authentication authentication
    ){
        return wishListImportService.importWishlist(
                authentication.getName(),
                request.url()
        );
    }

    @PostMapping("/users/me/wishlist/{itemId}/image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createImage(
            @PathVariable UUID itemId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        wishListImageService.create(
                authentication.getName(),
                itemId,
                file
        );
    }
    @GetMapping("/wishlist/{itemId}/image")
    public ResponseEntity<Resource> getImage(
            @PathVariable UUID itemId
    ) {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(wishListImageService.get(itemId));
    }
    @DeleteMapping("/users/me/wishlist/{itemId}/image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(
            @PathVariable UUID itemId,
            Authentication authentication
    ) {
        wishListImageService.delete(
                authentication.getName(),
                itemId
        );
    }
}
