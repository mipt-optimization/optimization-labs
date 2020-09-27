package ru.sberbank;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ru.sberbank.lab1.Lab1Controller;

@SpringBootApplication
@EnableScheduling
public class Optdemo1Application {

    @Autowired
    Lab1Controller lab1Controller;
    //С помощью этого метода мы можем переодически вызывать этот метод и кэшировать результаты
    @Scheduled(cron = "0 0 * * 3 *")//К примеру, каждую среду в полночь будем кжшировать последние 7 дней
    public void initTemperatureData() throws JSONException {
        lab1Controller.getWeatherForPeriod(7);
    }

    public static void main(String[] args) {
        SpringApplication.run(Optdemo1Application.class, args);
    }
}
