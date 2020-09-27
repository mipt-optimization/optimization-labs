package ru.sberbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableCaching
@SpringBootApplication
public class Optdemo1Application {

    public static void main(String[] args) {
        SpringApplication.run(Optdemo1Application.class, args);
    }
}
