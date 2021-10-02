package ru.sberbank.lab1;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
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
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import static java.util.Collections.emptyList;

@RestController
@RequestMapping("/lab1")
public class Lab1Controller {

    private static final String URL = "http://export.rbc.ru/free/selt.0/free.fcgi?period=DAILY&tickers=USD000000TOD&separator=TAB&data_format=BROWSER";

    // это константа, давайте её и задавать как константу, а не вычислять каждый раз
    // хотя по идее джава соптимизирует и в compile-time всё должна посчитать, но зачем на неё надеяться
    private static final long ONE_DAY_IN_SEC = 24 * 60 * 60L;

    private final CacheWeatherGetter weatherGetter;

    public Lab1Controller() {
        weatherGetter = new CacheWeatherGetter();
    }

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
        // Это ужасно, делать N запросов по сети последовательно друг за другом
        // В идеале бы найти в API запрос, который бы за раз все данные вернул
        // Но у них на сайте я нормального описания API не нашёл, поэтому давайте хотя бы
        // запустим это дело в параллель

        // сразу зададим capacity листу, мы ж его знаем
        // ну и вообще хочу сразу массив из days элементов
        List<Double> temps = new ArrayList<>(days);
        for (int i = 0; i < days; i++) {
            temps.add(0.);
        }
        // и хочу синхронизированный лист, чтобы ничего не сломалось
        List<Double> syncTemps = Collections.synchronizedList(temps);

        // Список тредов, чтоб их всех дождаться
        List<Thread> threads = new ArrayList<>();

        // и ещё список, чтоб узнать, кидал ли кто из потоков Exception
        List<JSONException> exceptions = Collections.synchronizedList(new ArrayList<>());

        // эту штуку можно за пределы цикла вынести, она +- одинаковая на каждой итерации
        long currentDayInSec = Calendar.getInstance().getTimeInMillis() / 1000;

        // находим значения
        for (int i = 0; i < days; i++) {
            long curDateSec = currentDayInSec - i * ONE_DAY_IN_SEC;
            // если закешировано значение, то не будем создавать поток, а сразу положим его в список
            if (weatherGetter.isCached(curDateSec)) {
                syncTemps.set(i, weatherGetter.getTemperature(curDateSec));
            }
            // а в противном случае делаем поток
            else {
                Thread t = new TempGetterThread(i, syncTemps, exceptions, weatherGetter, curDateSec);
                t.start();
                threads.add(t);
            }
        }

        // ждём пока все потоки всё сделают
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
            }
        }

        // Кидаем exception если кто вдруг кинул
        if (!exceptions.isEmpty()) {
            throw exceptions.get(0);
        }

        return temps;
    }

    // Класс потока для нахождения температуры
    private static class TempGetterThread extends Thread {
        private final int index;
        private final List<Double> results;
        private final List<JSONException> excs;
        private final CacheWeatherGetter weatherGetter;
        private final long curDateSec;

        public TempGetterThread(int index, List<Double> results, List<JSONException> excs, CacheWeatherGetter weatherGetter,
                                long curDateSec) {
            this.index = index;
            this.results = results;
            this.excs = excs;
            this.weatherGetter = weatherGetter;
            this.curDateSec = curDateSec;
        }

        @Override
        public void run() {
            try {
                Double curTemp = weatherGetter.getTemperature(curDateSec);
                results.set(index, curTemp);
            } catch (JSONException e) {
                excs.add(e);
            }
        }
    }

}

