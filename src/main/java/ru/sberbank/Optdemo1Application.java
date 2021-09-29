package ru.sberbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class Optdemo1Application {
    public static void main(String[] args) {
        SpringApplication.run(Optdemo1Application.class, args);
    }
}
