package net.engineeringdigest.journalApp.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    @Tag(name="Home Api's",description = "To check on deployment wether backend working properly or not")
    public String home() {
        return "Welcome to Journal App!";
    }
}
