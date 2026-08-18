package ru.mafteroid.secretsanta.controller.api;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mafteroid.secretsanta.dto.GiftAssignmentResponse;
import ru.mafteroid.secretsanta.dto.ParticipantResponse;
import ru.mafteroid.secretsanta.service.ParticipantService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events/{eventId}/participants")
public class ParticipantApiController {
    private final ParticipantService participantService;
    public ParticipantApiController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @GetMapping()
    public List<ParticipantResponse> getParticipants(@PathVariable UUID eventId, Authentication authentication) {
        return participantService.findAllByEventId(eventId, authentication.getName());
    }

    @GetMapping("/me/assignment")
    public GiftAssignmentResponse getAssignment(@PathVariable UUID eventId, Authentication authentication) {
        return participantService.getMyAssignment(eventId, authentication.getName());
    }
    //TODO: join and leave
}
