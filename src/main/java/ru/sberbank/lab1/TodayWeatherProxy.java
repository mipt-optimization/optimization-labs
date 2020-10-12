package ru.sberbank.lab1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class TodayWeatherProxy {

    Lab1Controller lab1Controller;

    private final Map<String, Double> cacheDayTemp = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(TodayWeatherProxy.class);

    private static void log(String action, Object obj) {
        logger.info("{}: {}", action, obj);
    }

    private static void log(String action) {
        logger.info("{}", action);
    }

    public TodayWeatherProxy(Lab1Controller lab1Controller) {
        this.lab1Controller = lab1Controller;
    }

    /*
     * Заполняет кэш каждый день на 14 дней - уменьшает лейтенси при вызове клиентом
     * */
    @Async
    @Scheduled(cron = "0 0 12 * * ?")
    void fillComingDateMap() {
        log("cache for Day temperature shedulled");
        lab1Controller.getWeatherForPeriod(14);
    }

    public Double getTempForDay(Long curDateSec) throws JSONException {
        Long oneDayInSec = 86400L;
        Long curDay = curDateSec / oneDayInSec;

        /*
         * Вычисляем день чтобы использовать его в как ключ в прокси
         * */
        String curDayKey = curDay.toString();

        Double temp = cacheDayTemp.get(curDayKey);
        if (temp != null) {
            /*
             * Если было в кэше то значительно уменьшаем лейтенси из-за отсутствия вызова по сети
             * */
            log("Retrieved temperature '" + temp + "' from cache.");
        } else {
            /*
             * делаем вызов, помещаем в прокси
             * */
            String todayWeather = getTodayWeather(curDateSec.toString());
            temp = getTemperature(todayWeather);
            cacheDayTemp.put(curDayKey, temp);
        }
        return temp;
    }

    public void reset() {
        cacheDayTemp.clear();
    }

    public String getTodayWeather(String date) {
        String obligatoryForecastStart = "https://api.darksky.net/forecast/6df573445c320d1634a227266d1360a3/";
        String LAcoordinates = "34.053044,-118.243750,";
        String exclude = "exclude=daily";

        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = obligatoryForecastStart + LAcoordinates + date + "?" + exclude;
        log("requested by rest:" + fooResourceUrl);
        ResponseEntity<String> response = restTemplate.getForEntity(fooResourceUrl, String.class);
        String info = response.getBody();
        log("response from API:" + info);
        return info;
    }

    public Double getTemperature(String info) throws JSONException {
        JSONObject json = new JSONObject(info);
        String hourly = json.getString("hourly");
        JSONArray data = new JSONObject(hourly).getJSONArray("data");
        Double temp = new JSONObject(data.get(0).toString()).getDouble("temperature");

        return temp;
    }
}
