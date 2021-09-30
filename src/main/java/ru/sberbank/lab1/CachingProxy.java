package ru.sberbank.lab1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class CachingProxy {

    private static final String OBLIGATORY_FORECAST_START = "https://api.darksky.net/forecast/6df573445c320d1634a227266d1360a3/";
    private static final String LA_COORDINATES = "34.053044,-118.243750,";
    private static final String EXCLUDE = "exclude=daily";
    private static final Long ONE_DAY_IN_SEC = 86400L;
    Lab1Controller lab1Controller;

    private final Map<String, Double> cache = new HashMap<>();

    public CachingProxy(Lab1Controller lab1Controller) {
        this.lab1Controller = lab1Controller;
    }

    @Async
    @Scheduled(cron = "0 0 * * *") // кэш заполняется значениями температуры каждый день на 10 дней вперед
    void fillCache() {
        lab1Controller.getWeatherForPeriod(10);
    }

    public Double getDayTemperature(Long curDateSec) throws JSONException {
        String curDay = Long.toString(curDateSec / ONE_DAY_IN_SEC);
        Double temperature = cache.get(curDay);
        if (cache.get(curDay) == null) {
            String todayWeather = getTodayWeather(curDateSec.toString());
            temperature = getTemperature(todayWeather);
            cache.put(curDay, temperature);
        }
        return temperature;
    }

    private String getTodayWeather(String date) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = OBLIGATORY_FORECAST_START + LA_COORDINATES + date + "?" + EXCLUDE;
        ResponseEntity<String> response = restTemplate.getForEntity(fooResourceUrl, String.class);
        String info = response.getBody();
        System.out.println(info);
        return info;
    }

    private Double getTemperature(String info) throws JSONException {
        JSONObject json = new JSONObject(info);
        String hourly = json.getString("hourly");
        JSONArray data = new JSONObject(hourly).getJSONArray("data");

        return new JSONObject(data.get(0).toString()).getDouble("temperature");
    }
}
