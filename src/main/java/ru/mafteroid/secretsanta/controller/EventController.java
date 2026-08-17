package ru.mafteroid.secretsanta.controller;


import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import ru.mafteroid.secretsanta.dto.CreateEventForm;
import ru.mafteroid.secretsanta.entity.Event;
import ru.mafteroid.secretsanta.service.EventService;

@Controller
public class EventController {
    private final EventService eventService;
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }


    @PostMapping("/create")
    public String createEvent(CreateEventForm form, Authentication authentication) {
        Event event = eventService.create(form, authentication.getName());
        return "redirect:/room/" + event.getId();
    }

}
