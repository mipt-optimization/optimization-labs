package ru.sberbank.lab1;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/lab1")
public class OptimizedStep4Controller {

    RestTemplate restTemplate = new RestTemplate();

    // кэш в виде мапы
    static Map<Integer, Double> map = new HashMap<>();

    @GetMapping("/weather")
    public List<Double> getWeatherForPeriod(Integer days) {
        // (опт7 - распределение работы между потоками)

        return IntStream.range(0, days).parallel().mapToObj(i -> {
            if (map.containsKey(i)) {
                return map.get(i);
            }
            String s;
            s = Long.toString(Calendar.getInstance().getTimeInMillis() / 1000 - i * 24 * 60 * 60L);
            s = "https://api.darksky.net/forecast/3ce5ca6c6c64befaa69dd9cf05b939db/34.053044,-118.243750," + s + "?exclude=daily";
            s = restTemplate.getForEntity(s, String.class).getBody();
            try {
                Double d = (new JSONObject(new JSONObject(new JSONObject(s).getString("hourly")).getJSONArray("data").get(0).toString())).getDouble("temperature");
                map.put(i, d);
                return d;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());
    }
}
