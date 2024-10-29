package com.example.chatprer12_02.component;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnrichmentController {

    private int count = 0;

    @GetMapping("/enrich")
    public String enrich(){
        if (Math.random()>.5){
            throw new RuntimeException("oops");
        }

        this.count++;
        return "enriched " + this.count + " times";
    }
}
