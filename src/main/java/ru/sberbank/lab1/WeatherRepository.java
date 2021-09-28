package ru.sberbank.lab1;

import java.time.LocalDate;

public interface WeatherRepository {
    Double getTemperatureForDate(LocalDate date);
}
