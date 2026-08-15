package ru.mafteroid.secretsanta.controller;

import jakarta.servlet.Registration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import ru.mafteroid.secretsanta.dto.RegisterForm;
import ru.mafteroid.secretsanta.service.RegistrationService;

@Controller
public class RegistrationController {
    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/register")
    public String register(RegisterForm registerForm) {
        registrationService.register(registerForm);
        System.out.println("FORM: " + registerForm);
        return "redirect:/login";
    }
}
