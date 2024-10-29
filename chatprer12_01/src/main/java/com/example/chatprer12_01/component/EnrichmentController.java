package com.example.chatprer12_01.component;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnrichmentController {

    private int count = 0;

    @GetMapping("/enrich")
    public String enrich(){
        this.count++;
        return "enriched " + this.count + " times";
    }
}
