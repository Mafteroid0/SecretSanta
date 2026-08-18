package ru.mafteroid.secretsanta.controller;


import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.mafteroid.secretsanta.dto.CreateEventRequest;
import ru.mafteroid.secretsanta.entity.Event;
import ru.mafteroid.secretsanta.service.EventService;

import java.util.UUID;

@Controller
public class EventController {
    private final EventService eventService;
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }






}

