package ru.sberbank.lab1;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WebWeatherClient implements WeatherClient {
    private static final String obligatoryForecastStart = "https://api.darksky.net/forecast/3ce5ca6c6c64befaa69dd9cf05b939db/";
    private static final String LAcoordinates = "34.053044,-118.243750,";
    //добавил exclude properties
    private static final String exclude = "exclude=daily,currently,flags";
    //сделал RestTemplate статической финальной переменной
    private static final RestTemplate restTemplate = new RestTemplate();

    //добавил кеш (кеширует погоду за определенные день)
    @Override
    @Cacheable("weather")
    public String getWeatherFor(long dateInSec) {
        return restTemplate
                .getForEntity(url(dateInSec), String.class)
                .getBody();
    }

    private String url(long dateInSec) {
        return obligatoryForecastStart + LAcoordinates + dateInSec + "?" + exclude;
    }
}
