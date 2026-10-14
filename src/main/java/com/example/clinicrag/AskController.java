package com.example.clinicrag;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AskController {
    public record AskRequest(@NotBlank String question){}
    private final AskService askService;

    public AskController(AskService askService){
        this.askService = askService;
    }

    //validates the input, calls AskService, and returns the answer
    @PostMapping("/ask")
    public AskService.Answer ask(@Valid @RequestBody AskRequest req){
        return askService.ask(req.question());
    }
}
