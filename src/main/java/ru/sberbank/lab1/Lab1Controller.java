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

import javax.annotation.PreDestroy;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

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

    private void startup() {
        try {
            FileReader fileReader = new FileReader("cache.txt");
            Scanner scanner = new Scanner(fileReader);
            Integer i = 0;
            while (scanner.hasNextLong()) {
                System.out.println(i++);
                temperatureCache.put(scanner.nextLong(), scanner.nextDouble());
            }
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void destroy() { // Сохранение кэша в файл при остановке
        try {
            FileWriter fileWriter = new FileWriter("cache.txt");
            for (Long dayNumber : temperatureCache.keySet()) {
                fileWriter.write(dayNumber + " ");
                fileWriter.write(temperatureCache.get(dayNumber) + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @GetMapping("/weather")
    public List<Double> getWeatherForPeriod(Integer days) {
        if (temperatureCache.isEmpty()) {
            startup(); // Загрузка кэша из файла
        }
        try {
            return getTemperatureForLastDays(days);
        } catch (JSONException e) {
        }

        return emptyList();
    }

    ConcurrentHashMap<Long, Double> temperatureCache = new ConcurrentHashMap<>();

    public List<Double> getTemperatureForLastDays(int days) throws JSONException {
        List<Double> temps = new ArrayList<>();

        Long currentDay = Calendar.getInstance().getTimeInMillis() / 86_400_000L; // Объявление переменной вынес из цикла
        Long oneDayInSec = 24 * 60 * 60L; // Объявление переменной вынес из цикла
        for (int i = 0; i < days; ++i) {
            if (temperatureCache.containsKey(currentDay)) {
                temps.add(temperatureCache.get(currentDay)); // Сделал кэширование
            } else {
                Long currentDayInSec = currentDay * oneDayInSec;
                Double curTemp = getTemperatureFromInfo(currentDayInSec.toString());
                temps.add(curTemp);
                temperatureCache.put(currentDay, curTemp);
            }
            --currentDay;
        }
        return temps;
    }


    public String getTodayWeather(String date) {
        String obligatoryForecastStart = "https://api.darksky.net/forecast/f92ee297dfdfd3a86f08a388f5ae83cc/";
        String LAcoordinates = "34.053044,-118.243750,";
        String exclude = "exclude=daily";

        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = obligatoryForecastStart + LAcoordinates + date + "?" + exclude;
        System.out.println(fooResourceUrl);
        ResponseEntity<String> response = restTemplate.getForEntity(fooResourceUrl, String.class);
        String info = response.getBody();
//        System.out.println(info);
        return info;
    }

    public Double getTemperatureFromInfo(String date) throws JSONException {
        String info = getTodayWeather(date);
        Double curTemp = getTemperature(info);
        return curTemp;
    }

    public Double getTemperature(String info) throws JSONException {
        JSONObject json = new JSONObject(info);
        String hourly = json.getString("hourly");
        JSONArray data = new JSONObject(hourly).getJSONArray("data");
        Double temp = new JSONObject(data.get(0).toString()).getDouble("temperature");

        return temp;
    }
}

