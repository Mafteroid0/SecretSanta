package ru.mafteroid.secretsanta.controller;



import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Controller
public class PageController {
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
}
