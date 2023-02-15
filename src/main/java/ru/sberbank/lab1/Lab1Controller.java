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
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@RestController
@RequestMapping("/lab1")
public class Lab1Controller {

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
    public List<Double> getWeatherForPeriod(Integer days) {
        try {
            return getTemperatureForLastDays(days);
        } catch (JSONException e) {
        }

        return emptyList();
    }

    public List<Double> getTemperatureForLastDays(int days) throws JSONException {
        List<Future<Double>> futures = new ArrayList<>(days);
        for (int i = 0; i < days; ++i) {
            long curTimeSeconds = Calendar.getInstance().getTimeInMillis() / 1000;
            long targetTimeSeconds = curTimeSeconds - SECONDS_PER_DAY * i;
            String targetDateString = Long.toString(targetTimeSeconds);
            futures.add(getWeatherForDateAsync(targetDateString));
        }
        return futures.stream().map(future -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                System.out.println(e.toString());
                return null;
            }
        }).collect(Collectors.toList());
    }

    public Future<Double> getWeatherForDateAsync(String date) {
        String url = getWeatherRequestUrl(date);
        // 1. a straightforward parallelization
        return weatherExecutor.submit(() -> {
            // 2. instantiating a separate RestTemplate for every request is inefficient, let's use a single one;
            //    it's thread-safe
            ResponseEntity<String> response = weatherRestTemplate.getForEntity(url, String.class);
            String info = response.getBody();
            return getTemperatureFromInfo(info);
        });
    }

    public Double getTemperatureFromInfo(String info) throws JSONException {
        JSONObject json = new JSONObject(info);
        String hourly = json.getString("hourly");
        JSONArray data = new JSONObject(hourly).getJSONArray("data");

        return new JSONObject(data.get(0).toString()).getDouble("temperature");
    }

    private static String getWeatherRequestUrl(String date) {
        return WEATHER_BASE_URL + "/" + TOKEN + "/" + LA_COORDINATES + "," + date + "?" + String.join("&", PARAMS);
    }

    private static final String WEATHER_BASE_URL = "https://api.darksky.net/forecast";
    private static final String TOKEN = "ac1830efeff59c748d212052f27d49aa";
    private static final String LA_COORDINATES = "34.053044,-118.243750";
    private static final List<String> PARAMS = Collections.singletonList("exclude=daily");
    private static final long SECONDS_PER_DAY = 24L * 60 * 60;
    private static final ExecutorService weatherExecutor = Executors.newFixedThreadPool(4);
    private static final RestTemplate weatherRestTemplate = new RestTemplate();
}

