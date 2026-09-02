package ru.mafteroid.secretsanta.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.mafteroid.secretsanta.dto.RegisterForm;
import ru.mafteroid.secretsanta.entity.User;
import ru.mafteroid.secretsanta.exceptions.ConflictException;
import ru.mafteroid.secretsanta.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks RegistrationService registrationService;

    @Test
    void hashesPasswordAndSavesUser() {
        RegisterForm form = new RegisterForm("dan", "Даня", "password123");
        when(passwordEncoder.encode(form.password())).thenReturn("encoded-password");

        registrationService.register(form);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertAll(
                () -> assertEquals("dan", saved.getUsername()),
                () -> assertEquals("Даня", saved.getDisplayName()),
                () -> assertEquals("encoded-password", saved.getPasswordHash())
        );
    }

    @Test
    void rejectsDuplicateUsername() {
        RegisterForm form = new RegisterForm("dan", "Даня", "password123");
        when(userRepository.existsByUsernameIgnoreCase("dan")).thenReturn(true);

        assertThrows(ConflictException.class, () -> registrationService.register(form));

        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any());
    }
}
