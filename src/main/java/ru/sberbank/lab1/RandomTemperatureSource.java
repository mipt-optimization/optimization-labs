package ru.sberbank.lab1;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

import static java.lang.Thread.sleep;

@Service
@RequiredArgsConstructor
public class RandomTemperatureSource implements TemperatureSource {
    @Value("${randomSource.sleepMillis}")
    private long sleepMillis;

    @SneakyThrows
    public double get(long hourTimestamp) {
        sleep(sleepMillis);
        return new Random(hourTimestamp).nextDouble();
    }
}
