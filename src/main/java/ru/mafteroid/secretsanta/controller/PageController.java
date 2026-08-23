package ru.mafteroid.secretsanta.controller;



import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.mafteroid.secretsanta.entity.Event;
import ru.mafteroid.secretsanta.entity.User;
import ru.mafteroid.secretsanta.repository.EventParticipantRepository;
import ru.mafteroid.secretsanta.service.EventService;
import ru.mafteroid.secretsanta.service.UserService;

import java.util.List;
import java.util.UUID;

@Controller
public class PageController {
    private final EventService eventService;
    private final UserService userService;
    private final EventParticipantRepository eventParticipantRepository;

    public PageController(EventService eventService, UserService userService, EventParticipantRepository eventParticipantRepository) {
        this.eventService = eventService;
        this.userService = userService;
        this.eventParticipantRepository = eventParticipantRepository;
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
    public String games() {
//        String username = authentication.getName();
//        List<Event> events = eventService.findEventsByUsername(username);
//        model.addAttribute("events", events);
        return "games";
    }

    @GetMapping("/create")
    public String create(){
        return "create";
    }

    @GetMapping("/room/{eventId}")
    public String room(@PathVariable UUID eventId) {
//        Event event = eventService.findEventById(UUID.fromString(eventId));
//        model.addAttribute("event", event);
//        List<User> users = userService.findUsersByEventId(event.getId());
//        model.addAttribute("users", users);
        return "room";

    }

    @GetMapping("/room")
    public String noRoom(){
        return "redirect:/games";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        User user = userService.getUserByUsername(authentication.getName());
        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/profile/{username}")
    public String profile(Authentication authentication, @PathVariable String username, Model model) {
        User user = userService.getUserByUsername(username);
        model.addAttribute("user", user);
        if(authentication.getName().equals(username)) {
            return "redirect:/profile";
        }
        return "profile";
    }

}
