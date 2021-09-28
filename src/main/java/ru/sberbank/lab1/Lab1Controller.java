package ru.sberbank.lab1;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

import static java.util.Collections.emptyList;

@RestController
@RequestMapping("/lab1")
public class Lab1Controller {
    //TODO thread-safe, поэтому будем юзать один экземпляр, а не создавать на каждый запрос новый
    private final RestTemplate restTemplate = new RestTemplate();
    private final TemperatureCache temperatureCache;

    public Lab1Controller(TemperatureCache temperatureCache) { this.temperatureCache = temperatureCache; }

    @EventListener(ApplicationReadyEvent.class)
    public void heatingCache() {
        try { getTemperatureForLastDays(300); } catch (JSONException e) { System.out.println("Cache not heating"); }
        System.out.println("Cache heating");
    }

    @GetMapping("/weather")
    public List<Double> getWeatherForPeriod(int days) {
        try { return getTemperatureForLastDays(days); } catch (JSONException ignore) { return emptyList(); }
    }

    public List<Double> getTemperatureForLastDays(int days) throws JSONException {
        List<Double> temperatureFromCache;
        synchronized (temperatureCache) { temperatureFromCache = temperatureCache.getTemperature(days); }
        if (temperatureFromCache != null) return temperatureFromCache;

        //TODO инициализацию времени вынес из цикла + получаем время начала дня
        long oneDayInSec = 24 * 60 * 60L;
        long currentDayInSec = LocalDate.now().toEpochDay() * oneDayInSec;

        //TODO capacity массива сразу под нужный размер (избегаем выделения размера на этапе заполнения массива)
        List<Double> temps = new ArrayList<>(days);
        for (int i = 0; i < days; i++) temps.add(getTemperatureFromInfo(currentDayInSec - i * oneDayInSec));

        synchronized (temperatureCache) { temperatureCache.syncCache(temps); }
        return temps;
    }

    //TODO тип параметра сразу в лонг
    public String getTodayWeather(long date) {
        String obligatoryForecastStart = "https://api.darksky.net/forecast/7ba6164198e89cb2e6b2454d90e7b41d/";
        String LAcoordinates = "34.053044,-118.243750,";
        //TODO Добавил еще эксклюдов для увеличения производительности сервера и снижения размера сообщения от сервера
        String exclude = "exclude=currently,minutely,daily,alerts,flags";
        String fooResourceUrl = obligatoryForecastStart + LAcoordinates + date + "?" + exclude;
        return restTemplate.getForEntity(fooResourceUrl, String.class).getBody();
    }

    //TODO тип параметра сразу в лонг
    public Double getTemperatureFromInfo(long date) throws JSONException {
        String info = getTodayWeather(date);
        return getTemperature(info);
    }

    public Double getTemperature(String info) throws JSONException {
        //TODO в одну строку, без создания промежуточной строки
        return new JSONObject(info).getJSONObject("hourly").getJSONArray("data").getJSONObject(0).getDouble("temperature");
    }
}

