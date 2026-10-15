package com.example.clinicrag;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//POST /api/ask  with JSON like {"question": "Can a visitor stay overnight?"}
// Returns the answer, the source files used, and how long it took.
@RestController
@RequestMapping("/api")
public class AskController {
    public record AskRequest(@NotBlank(message = "question must not be blank")
                             @Size(max = 500, message = "question must be 500 characters or less")
                             String question){}
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
