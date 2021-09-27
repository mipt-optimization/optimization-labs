package ru.sberbank.lab1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
public class WeatherService {

    @Autowired
    RestTemplate template;

    @Autowired
    String uri;

    public List<Double> getTemperatureForLastDays(int days) throws JSONException {
        long oneDayInSec = 24 * 60 * 60L;
        long currentDayInSec = Calendar.getInstance().getTimeInMillis() / 1000;
        return LongStream.range(0, days)
                .parallel()
                .map(day -> currentDayInSec - day * oneDayInSec)
                .mapToObj(date -> getTemperatureFromInfo(Long.toString(date)))
                .collect(toList());
    }

    public Double getTemperatureFromInfo(String date) {
        String info = getTodayWeather(date);
        return getTemperature(info);
    }

    public String getTodayWeather(String date) {
        return template.getForEntity(uri, String.class, date).getBody();
    }

    public Double getTemperature(String info) {
        try {
            return new JSONObject(info)
                    .getJSONObject("hourly")
                    .getJSONArray("data")
                    .getJSONObject(0)
                    .getDouble("temperature");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
