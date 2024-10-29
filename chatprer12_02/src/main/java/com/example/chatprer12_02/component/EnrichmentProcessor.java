package com.example.chatprer12_02.component;

import com.example.chatprer12_02.domain.Foo;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class EnrichmentProcessor implements ItemProcessor<Foo, Foo> {

    @Autowired
    private RestTemplate restTemplate;

    @Recover
    public Foo fallback(Foo item) throws Exception {
        item.setMessage("error");
        return item;
    }

    @CircuitBreaker(maxAttempts = 1)
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
