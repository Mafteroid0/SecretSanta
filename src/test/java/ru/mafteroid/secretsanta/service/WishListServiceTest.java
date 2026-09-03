package ru.mafteroid.secretsanta.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mafteroid.secretsanta.dto.CreateWishListItemRequest;
import ru.mafteroid.secretsanta.entity.User;
import ru.mafteroid.secretsanta.entity.WishListItem;
import ru.mafteroid.secretsanta.repository.UserRepository;
import ru.mafteroid.secretsanta.repository.WishListItemRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishListServiceTest {

    @Mock WishListItemRepository itemRepository;
    @Mock UserRepository userRepository;
    @InjectMocks WishListService wishListService;

    @Test
    void createsItemAndNormalizesBlankDescription() {
        User user = mock(User.class);
        when(userRepository.findByUsernameIgnoreCase("dan")).thenReturn(Optional.of(user));

        wishListService.create("dan", new CreateWishListItemRequest("Клавиатура", "   "));

        ArgumentCaptor<WishListItem> captor = ArgumentCaptor.forClass(WishListItem.class);
        verify(itemRepository).save(captor.capture());
        assertEquals("Клавиатура", captor.getValue().getName());
        assertNull(captor.getValue().getDescription());
    }

    @Test
    void deletesOwnItem() {
        UUID userId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        User user = userWithId(userId);
        WishListItem item = itemOwnedBy(userId);
        when(userRepository.findByUsernameIgnoreCase("dan")).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        wishListService.delete("dan", itemId);

        verify(itemRepository).delete(item);
    }

    @Test
    void rejectsDeletingAnotherUsersItem() {
        UUID itemId = UUID.randomUUID();
        User user = userWithId(UUID.randomUUID());
        WishListItem item = itemOwnedBy(UUID.randomUUID());
        when(userRepository.findByUsernameIgnoreCase("dan")).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(RuntimeException.class, () -> wishListService.delete("dan", itemId));

        verify(itemRepository, never()).delete(any());
    }

    private User userWithId(UUID id) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(id);
        return user;
    }

    private WishListItem itemOwnedBy(UUID userId) {
        WishListItem item = mock(WishListItem.class);
        User owner = userWithId(userId);

        when(item.getUser()).thenReturn(owner);

        return item;
    }
}
