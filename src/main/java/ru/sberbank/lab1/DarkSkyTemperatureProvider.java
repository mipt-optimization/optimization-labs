package ru.sberbank.lab1;

import lombok.SneakyThrows;
import org.json.JSONObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static java.lang.String.format;

@Service
public class DarkSkyTemperatureProvider implements TemperatureProvider {
    private final static String BASE_URL = "https://api.darksky.net/forecast/3ce5ca6c6c64befaa69dd9cf05b939db/";
    private final static String LA_COORDINATES = "34.053044,-118.243750,";
    private final static String EXCLUDE = "exclude=daily";

    private final static String urlTemplate = BASE_URL + LA_COORDINATES + "%s?" + EXCLUDE;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    @Cacheable(value = "temperatures", key = "#timestamp")
    public Double getTemperature(String timestamp) {
        String info = getTodayWeather(timestamp);
        return getTemperatureFromInfo(info);
    }

    public String getTodayWeather(String timestamp) {
        String url = format(urlTemplate, timestamp);
        return restTemplate.getForObject(url, String.class);
    }

    @SneakyThrows
    public Double getTemperatureFromInfo(String info) {
        return new JSONObject(info)
                .getJSONObject("hourly")
                .getJSONArray("data")
                .getJSONObject(0)
                .getDouble("temperature");
    }
}
