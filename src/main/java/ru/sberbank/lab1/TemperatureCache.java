package ru.sberbank.lab1;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TemperatureCache {
    private static final int MAX_CACHE_SIZE = 300;
    private List<Double> cache = null;
    private LocalDate cacheDate = null;

    public List<Double> getTemperature(int days) {
        if (!LocalDate.now().equals(cacheDate)) return null;
        if (cache.size() < days) return null;
        return cache.subList(cache.size() - days, cache.size());
    }

    public void syncCache(List<Double> newCache) {
        LocalDate currentDate = LocalDate.now();
        if (cacheDate != null && cacheDate.equals(currentDate) && newCache.size() <= cache.size() && cache.size() < MAX_CACHE_SIZE)
            return;

        cacheDate = currentDate;
        if (newCache.size() > MAX_CACHE_SIZE)
            cache = newCache.subList(newCache.size() - MAX_CACHE_SIZE, newCache.size());
        else
            cache = newCache;
    }
}
