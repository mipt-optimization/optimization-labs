package ru.sberbank.lab1;

import org.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping("/lab1")
public class Lab1Controller {


    @GetMapping("/weather")
    public List<Double> getWeatherForPeriod(Integer days) {

        return getTemperatureForLastDays(days);
    }

    private List<Double> getTemperatureForLastDays(int days) {
        List<Double> temps = new ArrayList<>();
        long oneDayInSec = 24 * 60 * 60L;

        for (int i = 0; i < days; i++) {
            long currentDayInSec = Calendar.getInstance().getTimeInMillis() / 1000;
            long currDateSeconds = currentDayInSec - i * oneDayInSec;
            Double curTemp = getTemperatureFromService(Long.toString(currDateSeconds));
            temps.add(curTemp);
        }

        return temps;
    }

    private Double getTemperatureFromService(String currDateSeconds) {
        String response = getTodayWeather(currDateSeconds);
        return extractTemperature(response);
    }

    private String getTodayWeather(String currDateSeconds) {
        String url = getRequestUrl(currDateSeconds);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return response.getBody();
    }

    private String getRequestUrl(String currDateSeconds) {
        String obligatoryForecastStart = "https://api.darksky.net/forecast/ac1830efeff59c748d212052f27d49aa/";
        String coordinates = "34.053044,-118.243750,";
        String exclude = "exclude=daily";
        return obligatoryForecastStart + coordinates + currDateSeconds + "?" + exclude;
    }

    private Double extractTemperature(String response) {
        ResponseParser parser = new ResponseParser(response);
        Double temp = null;
        try {
            temp = parser.extractTemperature();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp;
    }
}

