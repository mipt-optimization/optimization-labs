package ru.sberbank.lab1;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


//Class for caching temperature source
@Service
@RequiredArgsConstructor
public class CachedTemperatureSource implements TemperatureSource {
    @Qualifier("darkskyTemperatureSource")
    private final TemperatureSource temperatureSource;

    @Override
    @Cacheable("temperatures")
    public double get(long hourTimestamp) {
        return temperatureSource.get(hourTimestamp);
    }
}
