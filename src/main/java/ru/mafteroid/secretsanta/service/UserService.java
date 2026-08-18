package ru.mafteroid.secretsanta.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.mafteroid.secretsanta.dto.UserResponse;
import ru.mafteroid.secretsanta.entity.User;
import ru.mafteroid.secretsanta.repository.EventParticipantRepository;
import ru.mafteroid.secretsanta.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final EventParticipantRepository eventParticipantRepository;
    public UserService(UserRepository userRepository, EventParticipantRepository eventParticipantRepository) {
        this.userRepository = userRepository;
        this.eventParticipantRepository = eventParticipantRepository;
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }


    public List<User> findUsersByEventId(UUID eventId) {
        return eventParticipantRepository.findAllByEvent_Id(eventId);

    }
}
