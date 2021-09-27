package ru.sberbank.lab1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.util.Collections.emptyList;

@RestController
@RequestMapping("/lab1")
public class Lab1Controller {

    @GetMapping("/weather")
    public List<Double> getWeatherForPeriod(Integer days) {
        try {
            return getTemperatureForLastDays(days);
        } catch (JSONException e) {
        }

        return emptyList();
    }

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

    public Double getTemperatureFromInfo(String date) throws JSONException {
        String info = getTodayWeather(date);
        Double curTemp = getTemperature(info);
        return curTemp;
    }

    public Double getTemperature(String info) throws JSONException {
        JSONObject json = new JSONObject(info);
        String hourly = json.getString("hourly");
        JSONArray data = new JSONObject(hourly).getJSONArray("data");
        Double temp = new JSONObject(data.get(0).toString()).getDouble("temperature");

        return temp;
    }
}

