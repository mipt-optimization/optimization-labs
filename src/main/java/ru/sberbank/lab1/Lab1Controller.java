package ru.sberbank.lab1;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static java.util.Collections.emptyList;

@RestController
@RequestMapping("/lab1")
public class Lab1Controller {

    private final Map<Long,Double> cache = new HashMap<>();
    private final String obligatoryForecastStart = "https://api.darksky.net/forecast/ac1830efeff59c748d212052f27d49aa/";
    private final String LAcoordinates = "34.053044,-118.243750,";
    private final String exclude = "exclude=daily";
    private final String hourly = "hourly";
    private final String data = "data";
    private final String temperature = "temperature";
    private final long oneDayInSec = 24 * 60 * 60L;
    private final String UsaLA = "America/Los_Angeles";

    private static final String URL = "http://export.rbc.ru/free/selt.0/free.fcgi?period=DAILY&tickers=USD000000TOD&separator=TAB&data_format=BROWSER";

    @GetMapping("/quotes")
    public List<Quote> quotes(@RequestParam("days") int days) throws ExecutionException, InterruptedException, ParseException {
        AsyncHttpClient client = AsyncHttpClientFactory.create(new AsyncHttpClientFactory.AsyncHttpClientConfig());
        Response response = client.prepareGet(URL + "&lastdays=" + days).execute().get();

        String body = response.getResponseBody();
        String[] lines = body.split("\n");

        List<Quote> quotes = new ArrayList<>();

        Map<String, Double> maxMap = new HashMap<>();

        for (int i = 0; i < lines.length; i++) {
            String[] line = lines[i].split("\t");
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(line[1]);
            String year = line[1].split("-")[0];
            String month = line[1].split("-")[1];
            String monthYear = year + month;
            Double high = Double.parseDouble(line[3]);

            Double maxYear = maxMap.get(year);
            if (maxYear == null || maxYear < high) {
                maxMap.put(year, high);
                if (maxYear != null) {
                    List<Quote> newQuotes = new ArrayList<>();
                    for (Quote oldQuote : quotes) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(oldQuote.getDate());
                        int oldYear = cal.get(Calendar.YEAR);
                        if (oldYear == Integer.parseInt(year)) {
                            if (oldQuote.getMaxInYear() < high) {
                                Quote newQuote = oldQuote.setMaxInYear(high);
                                newQuotes.add(newQuote);
                            } else {
                                newQuotes.add(oldQuote);
                            }
                        }
                    }
                    quotes.clear();
                    quotes.addAll(newQuotes);
                }
            }

            Double maxMonth = maxMap.get(monthYear);
            if (maxMonth == null || maxMonth < high) {
                maxMap.put(monthYear, high);
                if (maxMonth != null) {
                    List<Quote> newQuotes = new ArrayList<>();
                    for (Quote oldQuote : quotes) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(oldQuote.getDate());
                        int oldYear = cal.get(Calendar.YEAR);
                        int oldMonth = cal.get(Calendar.MONTH);
                        if (oldYear == Integer.parseInt(year) && oldMonth == Integer.parseInt(month)) {
                            if (oldQuote.getMaxInMonth() < high) {
                                Quote newQuote = oldQuote.setMaxInMonth(high);
                                quotes.remove(oldQuote);
                                quotes.add(newQuote);
                            }
                        }
                    }
                }
            }

            Quote quote = new Quote(line[0],
                    new SimpleDateFormat("yyyy-MM-dd").parse(line[1]),
                    Double.parseDouble(line[2]),
                    Double.parseDouble(line[3]),
                    Double.parseDouble(line[4]),
                    Double.parseDouble(line[5]),
                    Long.parseLong(line[6]),
                    Double.parseDouble(line[7]));
            quote = quote.setMaxInMonth(maxMap.get(monthYear));
            quote = quote.setMaxInYear(maxMap.get(year));

            quotes.add(quote);
        }
        return quotes;
    }

    @GetMapping("/weather")
    public List<Double> getWeatherForPeriod(Integer days) throws JSONException {
        return getTemperatureForLastDays(days);
    }

    public long get7AMInLA () {
        ZoneId zone = ZoneId.of(UsaLA);
        LocalDateTime sevenAMInLA = LocalDate.now().atStartOfDay();
        ZoneOffset zoneOffSet = zone.getRules().getOffset(sevenAMInLA);
        return sevenAMInLA.toEpochSecond(zoneOffSet);
    }

    public List<Double> getTemperatureForLastDays(int days) throws JSONException {
        List<Double> temps = new ArrayList<>();
        long timeInLA = get7AMInLA();
        double curTemp;
        for (int i = 0; i < days; i++) {
            timeInLA -= i * oneDayInSec;
            if (cache.containsKey(timeInLA)) {
                temps.add(cache.get(timeInLA));
            } else {
                curTemp = getTemperatureFromInfo(Long.toString(timeInLA));
                cache.put(timeInLA,curTemp);
                temps.add(curTemp);
            }
        }

        return temps;
    }

    public String getTodayWeather(String date) {
        RestTemplate restTemplate = new RestTemplate();
        StringBuilder fooResourceUrl = new StringBuilder()
                .append(obligatoryForecastStart)
                .append(LAcoordinates)
                .append(date)
                .append("?")
                .append(exclude);
        System.out.println(fooResourceUrl);
        ResponseEntity<String> response = restTemplate.getForEntity(fooResourceUrl.toString(), String.class);
        String info = response.getBody();
        System.out.println(info);
        return info;
    }

    public Double getTemperatureFromInfo(String date) throws JSONException {
        return getTemperature(getTodayWeather(date));
    }

    public Double getTemperature(String info) throws JSONException {
        return new JSONObject(info)
                .getJSONObject(hourly)
                .getJSONArray(data)
                .getJSONObject(0)
                .getDouble(temperature);
    }
}

