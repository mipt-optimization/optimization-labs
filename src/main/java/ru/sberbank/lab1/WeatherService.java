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

@Service
public class WeatherService {

    @Autowired
    RestTemplate template;

    @Autowired
    String uri;

    public List<Double> getTemperatureForLastDays(int days) throws JSONException {
        List<Double> temps = new ArrayList<>();

        for (int i = 0; i < days; i++) {
            Long currentDayInSec = Calendar.getInstance().getTimeInMillis() / 1000;
            Long oneDayInSec = 24 * 60 * 60L;
            Long curDateSec = currentDayInSec - i * oneDayInSec;
            Double curTemp = getTemperatureFromInfo(curDateSec.toString());
            temps.add(curTemp);
        }

        return temps;
    }

    public String getTodayWeather(String date) {
        return template.getForEntity(uri, String.class, date).getBody();
    }

    public Double getTemperatureFromInfo(String date) throws JSONException {
        String info = getTodayWeather(date);
        Double curTemp = getTemperature(info);
        return curTemp;
    }

    public Double getTemperature(String info) throws JSONException {
         return new JSONObject(info)
                 .getJSONObject("hourly")
                 .getJSONArray("data")
                 .getJSONObject(0)
                 .getDouble("temperature");
    }
}
