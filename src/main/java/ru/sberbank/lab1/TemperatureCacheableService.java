package ru.sberbank.lab1;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Service
public class TemperatureCacheableService {

    @Cacheable("temperatureByDate")
    public Double getTemperature(LocalDate date) {
        long oneDayInSec = 24 * 60 * 60L;
        try {
            String info = getTodayWeather(date.toEpochDay() * oneDayInSec);
            return getTemperature(info);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getTodayWeather(long date) {
        String obligatoryForecastStart = "https://api.darksky.net/forecast/7ba6164198e89cb2e6b2454d90e7b41d/";
        String LAcoordinates = "34.053044,-118.243750,";
        //TODO Добавил еще эксклюдов для увеличения производительности сервера и снижения размера сообщения от сервера
        String exclude = "exclude=currently,minutely,daily,alerts,flags";
        String fooResourceUrl = obligatoryForecastStart + LAcoordinates + date + "?" + exclude;

        return new RestTemplate().getForEntity(fooResourceUrl, String.class).getBody();
    }

    public Double getTemperature(String info) throws JSONException {
        //TODO в одну строку, без создания промежуточной строки
        return new JSONObject(info).getJSONObject("hourly").getJSONArray("data").getJSONObject(0).getDouble("temperature");
    }
}
