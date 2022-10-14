package ru.sberbank.lab1;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WeatherWebClientConfiguration {
    @Value("${weather.base.url:https://api.darksky.net/forecast/}")
    private String baseUrl;
    @Value("${weather.auth.token:3ce5ca6c6c64befaa69dd9cf05b939db}")
    private String authToken;
    @Value("${weather.location:/34.053044,-118.243750,}")
    private String location;

    @Bean
    public WebClient weatherWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl + authToken + location)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}