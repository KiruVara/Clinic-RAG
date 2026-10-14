package com.example.clinicrag;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class IngestController {
    //fields
    private final IngestionService ingestionService;

    //constructor
    public IngestController(IngestionService ingestionService){
        this.ingestionService = ingestionService;
    }

    //api endpoint
    @PostMapping("/ingest")
    public Map<String, Integer> ingest() throws IOException{
        return Map.of("chunksStored", ingestionService.ingestAll());
    }

}
