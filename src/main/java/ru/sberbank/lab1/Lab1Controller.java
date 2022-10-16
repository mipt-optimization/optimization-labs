package ru.sberbank.lab1;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/lab1")
public class Lab1Controller {

    private static final String QUOTES_URL = "http://export.rbc.ru/free/selt.0/free.fcgi?period=DAILY&tickers=USD000000TOD&separator=TAB&data_format=BROWSER";
    public static final String FORECAST_BASE_URL = "https://api.darksky.net/forecast/7ba6164198e89cb2e6b2454d90e7b41d/";
    public static final long SECONDS_IN_ONE_DAY = 24 * 60 * 60L;
    public static final String LA_COORDINATES = "34.053044,-118.243750,";

    private final WebClient webClient;

    public Lab1Controller(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(FORECAST_BASE_URL).build();
    }

    @GetMapping("/quotes")
    public List<Quote> quotes(@RequestParam("days") int days) throws ExecutionException, InterruptedException, ParseException {
        AsyncHttpClient client = AsyncHttpClientFactory.create(new AsyncHttpClientFactory.AsyncHttpClientConfig());
        Response response = client.prepareGet(QUOTES_URL + "&lastdays=" + days).execute().get();

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
        return getTemperatureForLastDays(days)
                .collectList()
                .block();
    }

    public Flux<Double> getTemperatureForLastDays(int days) {
        // 1. Используем примитивные типы вместо оберток
        long currentDayInSeconds = Calendar.getInstance().getTimeInMillis() / 1000;

        // 2. Используем Reactive Streams + Spring WebClient для параллельной обработки нескольких запросов
        return Flux.range(0, days)
                .map(dayIndex -> currentDayInSeconds - dayIndex * SECONDS_IN_ONE_DAY)
                .flatMapSequential(dayTimestamp -> getTemperatureByDay(dayTimestamp.toString()));
    }

    public Mono<Double> getTemperatureByDay(String date) {
        Mono<String> responseBody = getTodayWeather(date);
        return getTemperature(responseBody);
    }

    public Mono<String> getTodayWeather(String date) {
        // 3. Исключим лишние поля из ответа на стороне поставщика
        String exclude = "exclude=currently,minutely,daily,flags";

        String fooResourceUrl = LA_COORDINATES + date + "?" + exclude;

        return webClient.get()
                .uri(fooResourceUrl)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(System.out::println);
    }

    public Mono<Double> getTemperature(Mono<String> info) {
        return info.map(body -> {
            try {
                JSONObject json = new JSONObject(body);
                String hourly = json.getString("hourly");
                JSONArray data = new JSONObject(hourly).getJSONArray("data");
                return new JSONObject(data.get(0).toString()).getDouble("temperature");
            } catch (JSONException e) {
            }

            return 0.0;
        });
    }
}
