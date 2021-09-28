package ru.sberbank.lab1.repo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.function.Function;

@Configuration
public class DarkSkyWeatherRepositoryConfig {

    @Value("${weather.token}")
    String token;

    @Value("${weather.latitude}")
    String latitude;

    @Value("${weather.longitude}")
    String longitude;

    @Value("${weather.exclude}")
    String exclude;

    @Bean
    public Function<Long, String> darkSkyWeatherProducer() {
        return new Function<Long, String>() {
            private final RestTemplate template = new RestTemplate();
            private final String uri = "https://api.darksky.net/forecast/" +
                    token + "/" + latitude + "," + longitude + "," + "{date}" +
                    "?exclude=" + exclude;

            @Override
            public String apply(Long epochSeconds) {
                return template.getForEntity(uri, String.class, epochSeconds).getBody();
            }
        };
    }
}
