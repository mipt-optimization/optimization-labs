package ru.sberbank.lab1;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import static java.util.Collections.emptyList;


@RestController
@RequestMapping("/lab1")
public class Lab1Controller {


    ConcurrentHashMap<String, Double> cache = new ConcurrentHashMap<>();
    private static final String URL = "http://export.rbc.ru/free/selt.0/free.fcgi?period=DAILY&tickers=USD000000TOD&separator=TAB&data_format=BROWSER";
    private static final Long ONE_DAY_IN_SEC = 86400L;
    private static final String OBLIGATORY_FORECAST_START = "https://api.darksky.net/forecast/ac1830efeff59c748d212052f27d49aa/";
    private static final String LA_ACOORDINATES = "34.053044,-118.243750,";
    private static final String EXCLUDE = "exclude=daily";

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
            Long currentDayInSec = Calendar.getInstance().getTimeInMillis() / 1000;
            long currentDay = currentDayInSec / ONE_DAY_IN_SEC; // лучше использовать примитивы
            Double curTemp;
            // заинлайнили метод getTemperatureForLastDays, он больше нигде не используется(будем считать, что и не будет, можно избавиться от него
            //хотя конечно по-хорошему лучше делать не внутри rest метода, а чтобы сервис какой то этим занимался
            List<Double> temps = new ArrayList<>();
            // вынесли константы из цикла, их не нужно каждый раз заново высчитывать. oneDayInSec и вовсе вынесли из метода как константу статическую в классе,
            //избавались от ввода некоторых бессмысленных локальных временных переменных
            for (int i = 0; i < days; i++) {
                curTemp = cache.get(Long.toString(currentDay)); //используем String для ключа как один из самых оптимальных вариантов в java
                if (curTemp != null) { //хэшируем значения, чтобы не высчитывать их каждый раз заново
                    temps.add(curTemp);
                }
                else {
                    curTemp = getTemperature(getTodayWeather((currentDayInSec).toString()));
                    temps.add(curTemp);
                    cache.put(Long.toString(currentDay), curTemp);
                }
                currentDayInSec -= ONE_DAY_IN_SEC;
                currentDay = currentDayInSec / ONE_DAY_IN_SEC;
            }

            return temps;
        } catch (JSONException e) {
        }

        return emptyList();
    }


    public String getTodayWeather(String date) {
        // вынесли константы и убрали ненужную временную локальную переменную restTemplate
        // используем StringBuilder для более быстрой контакенации строк
        StringBuilder fooResourceUrl = new StringBuilder()
                .append(OBLIGATORY_FORECAST_START)
                .append(LA_ACOORDINATES)
                .append(date)
                .append("?")
                .append(EXCLUDE);
        System.out.println(fooResourceUrl);
        String body = new RestTemplate().getForEntity(fooResourceUrl.toString(), String.class).getBody();
        //убрали ненужные временные локальные переменные
        System.out.println(body);
        return body;
    }

    public Double getTemperature(String info) throws JSONException {
       // возможно это понимажает читаемость кода, но с точки зрения оптимизации, можно избавиться от лишних созданий локальный временных объектов

        return new JSONObject(new JSONObject(new JSONObject(info).getString("hourly"))
                .getJSONArray("data")
                .get(0)
                .toString())

                .getDouble("temperature");
    }
}

