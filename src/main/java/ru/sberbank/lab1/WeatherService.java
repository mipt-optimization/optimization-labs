package ru.sberbank.lab1;

import java.util.List;

public interface WeatherService {
    List<Double> getWeatherForPeriod(int days);
}
