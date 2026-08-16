package ru.mafteroid.secretsanta.service;

import org.springframework.stereotype.Service;
import ru.mafteroid.secretsanta.entity.Event;
import ru.mafteroid.secretsanta.entity.User;
import ru.mafteroid.secretsanta.repository.EventRepository;
import ru.mafteroid.secretsanta.repository.UserRepository;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public List<Event> findEventsByUsername(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() ->
                new IllegalArgumentException("no such user")
        );
        return eventRepository.findAllByOwnerId(user.getId());
    }
}
