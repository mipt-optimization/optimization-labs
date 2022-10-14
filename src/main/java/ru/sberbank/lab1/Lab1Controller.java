package ru.sberbank.lab1;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;
import org.asynchttpclient.util.HttpConstants;
import org.json.JSONException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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

    // (1) Самая жираня оптимизация, параллелим запросы (2s -> 640ms)
    private final AsyncHttpClient client = AsyncHttpClientFactory.create(new AsyncHttpClientFactory.AsyncHttpClientConfig());

    @GetMapping("/weather")
    public List<Double> getWeatherForPeriod(Integer days) {
        try {
            return getTemperatureForLastDays(days);
        } catch (JSONException e) {
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return emptyList();
    }

    public List<Double> getTemperatureForLastDays(int days) throws JSONException, ExecutionException, InterruptedException {
        List<Double> temps = new ArrayList<>();
        List<Future<Response>> tasks = new ArrayList<>();
        // (2) Создаем тежелый объект много раз, пусть тут живет (640ms -> 260ms)
        Long currentDayInSec = Calendar.getInstance().getTimeInMillis() / 1000;
        Long oneDayInSec = 24 * 60 * 60L;

        for (int i = 0; i < days; i++) {
            Long curDateSec = currentDayInSec - i * oneDayInSec;
            tasks.add(getTodayWeather(curDateSec.toString(), client));
        }

        for (int i = 0; i < days; i++) {
            Double curTemp = getTemperature(tasks.get(i).get().getResponseBody());
            temps.add(curTemp);
        }

        return temps;
    }

    public Future<Response> getTodayWeather(String date, AsyncHttpClient client) {
        String obligatoryForecastStart = "https://api.darksky.net/forecast/3ce5ca6c6c64befaa69dd9cf05b939db/";
        String LAcoordinates = "34.053044,-118.243750,";
        String exclude = "exclude=daily";
        // Тут наверно оптимизатор справится с созданием одной большой строки
        String fooResourceUrl = obligatoryForecastStart + LAcoordinates + date + "?" + exclude;
        Request getRequest = new RequestBuilder(HttpConstants.Methods.GET)
                .setUrl(fooResourceUrl)
                .build();
        return client.executeRequest(getRequest);
    }

    public Double getTemperature(String info) {
        // (3) Нам от джсона надо всего 5 байт, а парсили весь объект большой, говнокод, но цель сейчас оптимизациия (260ms -> 240ms)
        String anchor = "\"temperature\":";
        int doubleStart = info.indexOf(anchor, info.indexOf(anchor) + 1) + anchor.length();
        int doubleEnd = info.indexOf(",", doubleStart);
        return Double.parseDouble(info.substring(doubleStart, doubleEnd));
    }
}

