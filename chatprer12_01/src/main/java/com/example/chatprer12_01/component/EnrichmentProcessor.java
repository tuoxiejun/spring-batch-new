package com.example.chatprer12_01.component;

import com.example.chatprer12_01.domain.Foo;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class EnrichmentProcessor implements ItemProcessor<Foo, Foo> {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Foo process(Foo item) throws Exception {

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "http://localhost:8080/enrich",
                HttpMethod.GET,
                null,
                String.class
        );
        item.setMessage(responseEntity.getBody());
        return item;
    }
}
