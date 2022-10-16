package ru.sberbank.lab1;

import lombok.extern.slf4j.Slf4j;
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
import java.util.concurrent.*;

import static java.util.Collections.emptyList;

@RestController
@RequestMapping("/lab1")
@Slf4j
public class Lab1Controller {

    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    /*
    относительно не значительное ускорение. Но убрал создание RestTemplate перед каждым запросом
     */
    private static final RestTemplate restTemplate = new RestTemplate();

    private static final long oneDayInSec = 24 * 60 * 60L;

    @GetMapping("/weather")
    public List<Double> getWeatherForPeriod(Integer days) {
        try {
            return getTemperatureForLastDays(days);
        } catch (JSONException|InterruptedException|ExecutionException e) {
            System.out.println("Exception " + e.getMessage());
        }

        return emptyList();
    }

    public List<Double> getTemperatureForLastDays(int days) throws JSONException, InterruptedException, ExecutionException {
        List<Double> temps = new ArrayList<>();

        /*
        Все запросы не зависят друг от друга, а значит их можно безопасно распараллелить. Что и было сделано при помощи FixedThreadPool
         */
        List<Callable<Double>> tasks = new ArrayList<>();
        for (int i = 0; i < days; ++i) {
            final int curDay = i;
            tasks.add(() -> {
                long currentDayInSec = Calendar.getInstance().getTimeInMillis() / 1000;
                Long curDateSec = currentDayInSec - curDay * oneDayInSec;
                return getTemperatureFromInfo(curDateSec.toString());
            });
        }
        List<Future<Double>> responses = executor.invokeAll(tasks);

        for (int i = 0; i < days; ++i) {
            temps.add(responses.get(i).get());
        }

//       for (int i = 0; i < days; ++i) {
//            long currentDayInSec = Calendar.getInstance().getTimeInMillis() / 1000;
//            Long curDateSec = currentDayInSec - i * oneDayInSec;
//            temps.add(getTemperatureFromInfo(curDateSec.toString()));
//        }


        return temps;
    }

    public String getTodayWeather(String date) {
        String obligatoryForecastStart = "https://api.darksky.net/forecast/6df573445c320d1634a227266d1360a3/";
        String LAcoordinates = "34.053044,-118.243750,";
        /*
        Из запроса убрали все лишние данные, чтобы уменьшить количество данных, которые ходят по сети, а также
        уменьшить количество вычислений с другой стороны
         */
        String exclude = "exclude=minutely,flags,hourly,daily";
//        String exclude = "exclude=daily";

        String fooResourceUrl = obligatoryForecastStart + LAcoordinates + date + "?" + exclude;
        ResponseEntity<String> response = restTemplate.getForEntity(fooResourceUrl, String.class);
        return response.getBody();
    }

    public Double getTemperatureFromInfo(String date) throws JSONException {
        String info = getTodayWeather(date);
        return getTemperature(info);
    }

    public Double getTemperature(String info) throws JSONException {
        JSONObject json = new JSONObject(info);
        String currently = json.getString("currently");
        return new JSONObject(currently).getDouble("temperature");
    }
}

