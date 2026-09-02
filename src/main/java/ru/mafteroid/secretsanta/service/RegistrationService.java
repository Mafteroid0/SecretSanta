package ru.mafteroid.secretsanta.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mafteroid.secretsanta.dto.RegisterForm;
import ru.mafteroid.secretsanta.entity.User;
import ru.mafteroid.secretsanta.exceptions.ConflictException;
import ru.mafteroid.secretsanta.repository.UserRepository;

@Service
public class RegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public RegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(RegisterForm form) {
        if (userRepository.existsByUsernameIgnoreCase(form.username())){
            throw new ConflictException("This username is already in use");
        }
        String encodedPassword = passwordEncoder.encode(form.password());

        User user = new User(form.username(), form.displayName(), encodedPassword);
        userRepository.save(user);

    }


}
