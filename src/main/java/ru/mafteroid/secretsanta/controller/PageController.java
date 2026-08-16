package ru.mafteroid.secretsanta.controller;



import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.mafteroid.secretsanta.entity.Event;
import ru.mafteroid.secretsanta.service.EventService;

import java.util.List;

@Controller
public class PageController {
    private final EventService eventService;

    public PageController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/")
    public String index(Authentication authentication) {
        boolean loggedIn = (authentication != null
                            && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken));

        if (loggedIn) {
            return "redirect:/home";
        }

        return "landing";
    }

    @GetMapping("/login")
    public String app() {
        return "login";
    }

    @GetMapping("/home")
    public String login() {
        return "home";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/games")
    public String games(Authentication authentication, Model model) {
        String username = authentication.getName();
        List<Event> events = eventService.findEventsByUsername(username);
        model.addAttribute("events", events);
        return "games";
    }
}
