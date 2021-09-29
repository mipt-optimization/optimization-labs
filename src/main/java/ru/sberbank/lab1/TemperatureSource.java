package ru.sberbank.lab1;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TemperatureSource {
    private static final String token = "3ce5ca6c6c64befaa69dd9cf05b939db";
    private static final String LAcoordinates = "34.053044,-118.243750,";
    private static final String exclude = "exclude=currently,daily,flags";
    // вынес переменные в статические
    private static final String baseUrl = "https://api.darksky.net/forecast/" + token + "/" + LAcoordinates;
    private static final RestTemplate restTemplate = new RestTemplate();

    // добавил аннотацию для кэширования температуры для конкретного дня
    @Cacheable("temperatureCache")
    public Double getTemperature(String date) throws JSONException {
        // убрал sout
        String url = baseUrl + date + "?" + exclude;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return parseTemperature(response.getBody());
    }

    // убрал создание ненужных JSONObject
    private Double parseTemperature(String response) throws JSONException {
        return new JSONObject(response)
                .getJSONObject("hourly")
                .getJSONArray("data")
                .getJSONObject(0)
                .getDouble("temperature");
    }
}
