package ru.sberbank.lab1;

import lombok.SneakyThrows;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DarkskyTemperatureSource implements TemperatureSource {
    @Override
    @SneakyThrows
    public double get(long hourTimestamp) {
        String info = getTodayWeather(hourTimestamp * (60 * 60));
        return getTemperature(info);
    }

    public String getTodayWeather(long timestampInSec) {
        return new RestTemplate()
                .getForEntity(url(timestampInSec), String.class)
                .getBody();
    }

    @SneakyThrows
    public Double getTemperature(String info) {
        String data = new JSONObject(info)
                .getJSONObject("hourly")
                .getJSONArray("data")
                .get(0).toString();
        return new JSONObject(data).getDouble("temperature");
    }

    private String url(long timestampInSec) {
        return "https://api.darksky.net/forecast/ac1830efeff59c748d212052f27d49aa/34.053044,-118.243750," + timestampInSec + "?exclude=daily";
    }
}
