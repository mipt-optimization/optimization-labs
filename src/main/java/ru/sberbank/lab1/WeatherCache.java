package ru.sberbank.lab1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Добавляем кеш для ускорения обработки запросов
@Component
public class WeatherCache {

    private Map<Long, Double> weathers;
    //Вообще надо бы его в Spring config вынести для полной оптимизации
    private RestTemplate restTemplate;
    //Убираем постоянный подсчет данной константы и сразу используем ее
    private static final Long ONE_DAY_IN_SEC = 86400L;
    // Вычисляем URL единожды(ну почти)
    private static final String RESOURCE_URL = "https://api.darksky.net/forecast/6df573445c320d1634a227266d1360a3/" + "34.053044,-118.243750,";


    public WeatherCache() {
        this.weathers = new HashMap<>();
        this.restTemplate = new RestTemplate();
    }
/**
 * 100 запросов это конечно супер круто при инициализации,
 * но у меня локально запускалось 24 секунды, имеет смысл снизить до 10 или сделать параметр настраевымым
**/
    @PostConstruct
    private void updateCache() throws JSONException {
        Long todaySeconds = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond() / 1000;
        for (int i = 0; i < 100; i++) {
            todaySeconds = todaySeconds - i * ONE_DAY_IN_SEC;
            weathers.put(todaySeconds, getTemperatureFromInfo(todaySeconds.toString()));
        }
    }

    public List<Double> getTemperatureForLastDays(int days) throws JSONException {
        Long todaySeconds = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond() / 1000;
        List<Double> temps = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            Long curDateSec = todaySeconds - i * ONE_DAY_IN_SEC;
            Double temp = weathers.get(curDateSec);
            if (temp == null) {
                temp = getTemperatureFromInfo(curDateSec.toString());
                weathers.put(curDateSec, temp);
            }
            temps.add(temp);
        }

        return temps;
    }

    public String getTodayWeather(String date) {
        String fooResourceUrl = RESOURCE_URL + date + "?" + "exclude=daily";
        ResponseEntity<String> response = restTemplate.getForEntity(fooResourceUrl, String.class);
        String info = response.getBody();
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
