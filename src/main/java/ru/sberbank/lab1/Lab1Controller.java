package ru.sberbank.lab1;

import org.asynchttpclient.AsyncHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping("/lab1")
public class Lab1Controller {

    @GetMapping("/quotes")
    public List<Quote> quotes(@RequestParam("days") int days) {
        AsyncHttpClient client = AsyncHttpClientFactory.create(new AsyncHttpClientFactory.AsyncHttpClientConfig());

        return null;
    } // метод не нужен в задаче и мозолит глаза

    List<Double> temps = new ArrayList<>();
    Long oneDayInSec = 24 * 60 * 60L; // вычисляется теперь лишь однажды, в то время как раньше вычислялось на каждой итерации
    Long currentDayInSec = Calendar.getInstance().getTimeInMillis() / 1000; // вызов Calendar заменен на System и
    // вынесен за скобки, так как время
    // выполнения запроса - около 3с для 10 дней и порядка минуты для 1000, что является верхним ограничением.
    // За это время погода не изменится.
    RestTemplate template = new RestTemplate();
    String forecastLA =
            "https://api.darksky.net/forecast/7ba6164198e89cb2e6b2454d90e7b41d/34.053044,-118.243750,";
    String excludeDaily = "?exclude=daily";
    Double temp;
    String hourly = "hourly";
    String data = "data";
    String temperature = "temperature";
    @GetMapping("/weather")
    public List<Double> getWeatherForPeriod(Integer days) {
        try {
            if (!temps.isEmpty()) {
                temps.clear();
            }
            getTemperatureFromInfo(); // начальная итерация цикла - так быстрее при малых значениях
            temps.add(temp); // начальная итерация цикла - так быстрее при малых значениях
            for (int i = 1; i < days; i++) {
                currentDayInSec -= oneDayInSec; // умножение заменено на вычитание
                getTemperatureFromInfo(); // убран вызов аргумента функции
                temps.add(temp);
            }
            return temps;
        } catch (JSONException ignored) {
        }

        return null;
    }
    // методы и временные переменные заинлайнены, остальные переменные вынесены в outer scope
    public void getTemperatureFromInfo() throws JSONException {
        //убрано два вызова sout, потому что они не нужны для программы
        temp = new JSONObject(new JSONObject(new JSONObject(
                template.getForEntity( forecastLA
                        + currentDayInSec.toString() + excludeDaily, String.class).getBody())
                .getString(hourly)).getJSONArray(data)
                .get(0).toString()).getDouble(temperature);
    }

}

