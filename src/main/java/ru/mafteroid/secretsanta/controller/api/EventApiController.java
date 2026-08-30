package ru.mafteroid.secretsanta.controller.api;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.mafteroid.secretsanta.dto.CreateEventRequest;
import ru.mafteroid.secretsanta.dto.EventResponse;
import ru.mafteroid.secretsanta.dto.ParticipantResponse;
import ru.mafteroid.secretsanta.service.EventService;
import ru.mafteroid.secretsanta.service.ParticipantService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
public class EventApiController {
    private final EventService eventService;
    private final ParticipantService participantService;

    public EventApiController(EventService eventService, ParticipantService participantService) {
        this.eventService = eventService;
        this.participantService = participantService;
    }

    @GetMapping
    public List<EventResponse> getMyEvents(Authentication authentication) {
        return eventService.findEventsByUsername(authentication.getName());
    }

    @GetMapping("/{eventId}")
    public EventResponse getEvent(@PathVariable UUID eventId, Authentication authentication) {
        return eventService.findEventById(eventId, authentication.getName());
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody CreateEventRequest eventRequest,
                                                     Authentication authentication) {
        EventResponse event = eventService.create(eventRequest, authentication.getName());
        URI location =
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(event.id())
                        .toUri();
        return ResponseEntity.created(location).body(event);
    }

    @PostMapping("/{eventId}/join")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void joinEvent(@PathVariable UUID eventId, Authentication authentication) {
        eventService.join(authentication.getName(), eventId);
    }


    @PostMapping("/{eventId}/start")
    public EventResponse startEvent(@PathVariable UUID eventId, Authentication authentication) {
        return eventService.start(eventId, authentication.getName());

    }
}
