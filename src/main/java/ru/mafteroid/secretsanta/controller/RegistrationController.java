package ru.mafteroid.secretsanta.controller;

import jakarta.servlet.Registration;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import ru.mafteroid.secretsanta.dto.RegisterForm;
import ru.mafteroid.secretsanta.service.RegistrationService;

@Controller
public class    RegistrationController {
    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/register")
    public String register(@Valid RegisterForm registerForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        registrationService.register(registerForm);

        return "redirect:/login";
    }
}
