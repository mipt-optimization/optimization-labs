package ru.sberbank.lab1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// Класс для нахождения температуры и её кеширования
public class CacheWeatherGetter {

    // По ключу даты в формате keyDateFormatter хранит закешированное значение температуры
    private final ConcurrentMap<String, Double> cache;

    private final SimpleDateFormat keyDateFormatter;

    public CacheWeatherGetter() {
        cache = new ConcurrentHashMap<>();
        // форматер для перевода unix-time в ключ в мапе
        keyDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    }

    /**
     * Если температура по данному дню хранится в кеше, то возвращает её.
     * В противном случае делает запрос на ресурс и возвращает результат, при этом кешируя его.
     * @param curDateSec отметка времени, по которой мы берём температуру
     * @return температура
     */
    public double getTemperature(long curDateSec) throws JSONException {
        String cacheKey = getCacheKey(curDateSec);
        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }
        double temperature = getTemperatureFromInfo(String.valueOf(curDateSec));
        cache.put(cacheKey, temperature);
        return temperature;
    }

    public boolean isCached(long curDateSec) {
        return cache.containsKey(getCacheKey(curDateSec));
    }

    private String getCacheKey(long curDateSec) {
        // умножается на 1000 потому что форматтер хочет время в миллисекундах
        return keyDateFormatter.format(curDateSec * 1000);
    }

    private String getTodayWeatherJson(String date) {
        String obligatoryForecastStart = "https://api.darksky.net/forecast/ac1830efeff59c748d212052f27d49aa/";
        String LAcoordinates = "34.053044,-118.243750,";
        String exclude = "exclude=daily";

        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = obligatoryForecastStart + LAcoordinates + date + "?" + exclude;
        System.out.println(fooResourceUrl);
        ResponseEntity<String> response = restTemplate.getForEntity(fooResourceUrl, String.class);
        String info = response.getBody();
        System.out.println(info);
        return info;
    }

    private double getTemperatureFromInfo(String date) throws JSONException {
        String jsonResponse = getTodayWeatherJson(date);
        return extractTemperatureFromJsonResponse(jsonResponse);
    }

    private double extractTemperatureFromJsonResponse(String jsonResponse) throws JSONException {
        JSONObject json = new JSONObject(jsonResponse);
        String hourly = json.getString("hourly");
        JSONArray data = new JSONObject(hourly).getJSONArray("data");
        return new JSONObject(data.get(0).toString()).getDouble("temperature");
    }
}
