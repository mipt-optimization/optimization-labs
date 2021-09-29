package ru.sberbank.lab1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
public class OptimizedStep1Controller {

    // (опт1 - вынос за цикл) создавать restTemplate вне цикла по дням
    RestTemplate restTemplate = new RestTemplate();

//    @GetMapping("/weather")
    public List<Double> getWeatherForPeriod(Integer days) {
        try {
            return getTemperatureForLastDays(days);
        } catch (JSONException e) {
        }

        return emptyList();
    }

    public List<Double> getTemperatureForLastDays(int days) throws JSONException {
        List<Double> temps = new ArrayList<>();

        // (опт1,2,3 - вынос за цикл, инлайнинг перем, анбоксинг) определить long переменные перед циклом,
        // чтобы не тратить время на их создание в скоупе цикла (days << 10000, поэтому код не успеет
        // прогреться, и эта оптимизация может ускорить код, выполняемый JVM в режиме интерпретатора)
        long currentDayInSec;
        long curDateSec;
        Double curTemp;

        for (int i = 0; i < days; i++) {
            currentDayInSec = Calendar.getInstance().getTimeInMillis() / 1000;
            curDateSec = currentDayInSec - i * 24 * 60 * 60L;
            curTemp = getTemperatureFromInfo(Long.toString(curDateSec), restTemplate);
            temps.add(curTemp);
        }
        return temps;
    }

    public String getTodayWeather(String date, RestTemplate restTemplate) {

        // (опт1 - вынос за цикл) создавать restTemplate  вне цикла по дням, а передавать сюда ссылку
        // (опт2 - инлайнинг перем) заинлайнить строковые переменные в единую строку-запрос
        // (опт4 - удаление sout) убрать принты

        String s = "https://api.darksky.net/forecast/3ce5ca6c6c64befaa69dd9cf05b939db/34.053044,-118.243750," + date + "?exclude=daily";
        return restTemplate.getForEntity(s, String.class).getBody();
    }

    public Double getTemperatureFromInfo(String date, RestTemplate restTemplate) throws JSONException {
        return getTemperature(getTodayWeather(date, restTemplate));
    }

    public Double getTemperature(String info) throws JSONException {
        JSONObject json = new JSONObject(info);
        String hourly = json.getString("hourly");
        JSONArray data = new JSONObject(hourly).getJSONArray("data");
        return new JSONObject(data.get(0).toString()).getDouble("temperature");
    }
}
