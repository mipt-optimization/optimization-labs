package ru.sberbank;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.springframework.boot.SpringApplication.run;

@EnableCaching
@EnableScheduling
@SpringBootApplication
public class Optdemo1Application {
    public static void main(String[] args) {
        run(Optdemo1Application.class, args);
    }
}
