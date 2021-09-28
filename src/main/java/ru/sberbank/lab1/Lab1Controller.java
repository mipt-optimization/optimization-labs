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

@RestController
@RequestMapping("/lab1")
public class Lab1Controller {

    private static final String URL = "http://export.rbc.ru/free/selt.0/free.fcgi?period=DAILY&tickers=USD000000TOD&separator=TAB&data_format=BROWSER";
    private static final long oneDayInSec = 24 * 60 * 60L;
    private static final String API_URL = "https://api.darksky.net/forecast/7ba6164198e89cb2e6b2454d90e7b41d/";
    private static final String COORDINATES = "34.053044,-118.243750,";
    private static final String API_ARG = "?exclude=daily";
    private static final Map<Long, Double> weatherCache = new HashMap<>();

    @GetMapping("/weather")
    public List<Double> getWeatherForPeriod(Integer days) {
        try {
            return getTemperatureForLastDays(days);
        } catch (JSONException e) {
        }

        return emptyList();
    }

    /*Добавим кэш, в котором хранятся температуры для дат, для которых уже были вызовы
    заменил Long на long для экономии памяти
    Сделал oneDayInSec константой, чтобы каждый раз ее не пересчитывать
    Вынес объявление currentDayInSec из цикла*/
    public List<Double> getTemperatureForLastDays(int days) throws JSONException {
        List<Double> temps = new ArrayList<>();
        long currentDayInSec = Calendar.getInstance().getTimeInMillis() / 1000;
        for (int i = 0; i < days; i++) {
            long curDateSec = currentDayInSec - i * oneDayInSec;
            long curDay = curDateSec / oneDayInSec;
            Double cachedValue = weatherCache.getOrDefault(curDay, null);
            if (cachedValue == null) {
                double curTemp = getTemperatureFromInfo(Long.toString(curDateSec));
                weatherCache.putIfAbsent(curDay, curTemp);
                temps.add(curTemp);
            } else {
                temps.add(cachedValue);
            }
        }

        return temps;
    }

    /*Заменим конкатенацию строк на StringBuilder, чтобы не создавались лишние строки
    Вынесем повторяющиеся строки в константы*/
    public String getTodayWeather(String date) {
        StringBuilder request = new StringBuilder();
        request.append(API_URL)
                .append(COORDINATES)
                .append(date)
                .append(API_ARG);

        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = request.toString();
        System.out.println(fooResourceUrl);
        ResponseEntity<String> response = restTemplate.getForEntity(fooResourceUrl, String.class);
        String info = response.getBody();
        System.out.println(info);
        return info;
    }

    /*Не создаем промежуточную переменную, сразу возращаем результат
    Поменял возвращаемый тип на double*/
    public double getTemperatureFromInfo(String date) throws JSONException {
        String info = getTodayWeather(date);
        return getTemperature(info);
    }

    /*Не создаем промежуточную переменную, сразу возращаем результат
    Поменял возвращаемый тип на double
    Убал лишние преобразования из String в JSonObject и обратно для экономии времени и памяти*/
    public double getTemperature(String info) throws JSONException {
        return new JSONObject(info)
                    .getJSONObject("hourly")
                    .getJSONArray("data")
                    .getJSONObject(0)
                    .getDouble("temperature");
    }
}

