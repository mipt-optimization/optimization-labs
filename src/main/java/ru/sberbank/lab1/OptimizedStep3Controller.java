package ru.sberbank.lab1;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static java.util.Collections.emptyList;

//@RestController
//@RequestMapping("/lab1")
public class OptimizedStep3Controller {

    RestTemplate restTemplate = new RestTemplate();

    // кэш в виде мапы
    static Map<Integer, Double> map = new HashMap<>();

//    @GetMapping("/weather")
    public List<Double> getWeatherForPeriod(Integer days) {
        try {
            // (опт6 - кэширование запросов)

            List<Double> temps = new ArrayList<>();
            long currentDayInSec;
            long curDateSec;
            double curTemp;
            String s;
            ResponseEntity<String> response;
            for (int i = 0; i < days; i++) {
                if (map.containsKey(i)) {
                    // достаем по запрошенному ранее дню
                    temps.add(map.get(i));
                    continue;
                }
                currentDayInSec = Calendar.getInstance().getTimeInMillis() / 1000;
                curDateSec = currentDayInSec - i * 24 * 60 * 60L;
                s = Long.toString(curDateSec);
                s = "https://api.darksky.net/forecast/3ce5ca6c6c64befaa69dd9cf05b939db/34.053044,-118.243750," + s + "?exclude=daily";
                response = restTemplate.getForEntity(s, String.class);
                s = response.getBody();
                curTemp = new JSONObject(new JSONObject(new JSONObject(s).getString("hourly")).getJSONArray("data").get(0).toString()).getDouble("temperature");
                temps.add(curTemp);
                map.put(i, curTemp); // заполняем кэш
            }
            return temps;
        } catch (JSONException e) {
            return emptyList();
        }
    }
}
