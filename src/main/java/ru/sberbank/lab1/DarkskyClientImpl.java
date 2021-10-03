package ru.sberbank.lab1;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class DarkskyClientImpl implements DarkskyClient {
    private final WebClient webClient = WebClient.create();

    @Override
    public Mono<String> get(String uri) {
        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class);
    }
}
