package ru.sberbank.lab1;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static java.lang.String.format;

@Service
public class DarkskyClientImpl implements DarkskyClient {
    private static final String darkskyUrlTemplate = "https://api.darksky.net/forecast/ac1830efeff59c748d212052f27d49aa/34.053044,-118.243750,%s?exclude=daily";
    private final WebClient webClient = WebClient.create();

    @Override
    public Mono<String> get(String date) {
        var url = format(darkskyUrlTemplate, date);
        System.out.println(url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class);
    }
}
