package ru.sberbank.lab1;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WeatherServiceConfig {

    @Value("${weather.token}")
    String token;

    @Value("${weather.latitude}")
    String latitude;

    @Value("${weather.longitude}")
    String longitude;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public String uri() {
        return "https://api.darksky.net/forecast/" +
                token + "/" + latitude + "," + longitude + "," + "{date}" +
                "?exclude=currently,minutely,daily,alerts,flags";
    }
}
