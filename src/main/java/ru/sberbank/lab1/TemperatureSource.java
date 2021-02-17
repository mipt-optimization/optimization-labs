package ru.sberbank.lab1;

public interface TemperatureSource {
    double get(long hourTimestamp);
}
