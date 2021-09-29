package ru.sberbank.lab1;

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

//@RestController
//@RequestMapping("/lab1")
public class OptimizedStep2Controller {

    RestTemplate restTemplate = new RestTemplate();

//    @GetMapping("/weather")
    public List<Double> getWeatherForPeriod(Integer days) {
        try {
            // (опт5 - инлайнинг функций)

            // inlined getTemperatureForLastDays
            List<Double> temps = new ArrayList<>();
            long currentDayInSec;
            long curDateSec;
            double curTemp;
            String s;
            ResponseEntity<String> response;
            for (int i = 0; i < days; i++) {
                currentDayInSec = Calendar.getInstance().getTimeInMillis() / 1000;
                curDateSec = currentDayInSec - i * 24 * 60 * 60L;
                // inlined getTemperatureFromInfo, getTodayWeather, getTemperature
                s = Long.toString(curDateSec);
                s = "https://api.darksky.net/forecast/3ce5ca6c6c64befaa69dd9cf05b939db/34.053044,-118.243750," + s + "?exclude=daily";
                response = restTemplate.getForEntity(s, String.class);
                s = response.getBody();
                curTemp = new JSONObject(new JSONObject(new JSONObject(s).getString("hourly")).getJSONArray("data").get(0).toString()).getDouble("temperature");
                temps.add(curTemp);
            }
            return temps;
        } catch (JSONException e) {
            return emptyList();
        }
    }
}
