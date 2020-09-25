package ru.sberbank.lab1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

public class TempCashing {

    private Lab1Controller lab1Controller;

    private final HashMap<String, Double> tempCache = new HashMap<>();

    public TempCashing(Lab1Controller lab1Controller) {
        this.lab1Controller = lab1Controller;
    }


    public Double getCurrentTemp(Long curDateSec) throws JSONException {
        Long oneDayInSec = 86400L;
        Long curDate = curDateSec / oneDayInSec;
        String strCurDay = curDate.toString();

        if (tempCache.containsKey(strCurDay)) {
            return tempCache.get(strCurDay);
        }
        else {
            Double curTemp = getTemperatureFromInfo(curDateSec.toString());
            tempCache.put(strCurDay, curTemp);
            return curTemp;
        }
    }


    private String getTodayWeather(String date) {
        String obligatoryForecastStart = "https://api.darksky.net/forecast/7ba6164198e89cb2e6b2454d90e7b41d/";
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


    private Double getTemperatureFromInfo(String date) throws JSONException {
        String info = getTodayWeather(date);
        Double curTemp = getTemperature(info);
        return curTemp;
    }


    private Double getTemperature(String info) throws JSONException {
        JSONObject json = new JSONObject(info);
        String hourly = json.getString("hourly");
        JSONArray data = new JSONObject(hourly).getJSONArray("data");
        Double temp = new JSONObject(data.get(0).toString()).getDouble("temperature");

        return temp;
    }
}
