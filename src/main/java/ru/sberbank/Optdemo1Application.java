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

    public static void main(String[] args) {
        SpringApplication.run(Optdemo1Application.class, args);
    }

    // Данный метод позволяет предварительно получить данные, чтобы была возможность закэшировать их.
    @Scheduled(fixedDelay = Long.MAX_VALUE)
    public void initTemperatureData() throws JSONException {
        lab1Controller.getTemperatureForLastDays(5);
    }
}
