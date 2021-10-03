package ru.sberbank.lab1;

import reactor.core.publisher.Mono;

public interface DarkskyClient {
    Mono<String> get(String uri);
}
