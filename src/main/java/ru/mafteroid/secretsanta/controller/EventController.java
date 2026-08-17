package ru.mafteroid.secretsanta.controller;


import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.mafteroid.secretsanta.dto.CreateEventForm;
import ru.mafteroid.secretsanta.entity.Event;
import ru.mafteroid.secretsanta.service.EventService;

import java.util.UUID;

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

    @PostMapping("/join")
    public String join(
            @RequestParam String eventId,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        UUID id;

        try {
            id = UUID.fromString(eventId);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "joinError",
                    "Некорректный ID игры"
            );

            return "redirect:/games";
        }

        try {
            eventService.join(authentication.getName(), id);
        } catch (IllegalArgumentException e) { //TODO: -> no event exception
            redirectAttributes.addFlashAttribute(
                    "joinError",
                    "Игра с таким ID не найдена"
            );

            return "redirect:/games";
        }

        return "redirect:/room/" + id;
    }

}

