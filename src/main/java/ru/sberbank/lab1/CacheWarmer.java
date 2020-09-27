package ru.sberbank.lab1;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;


import static java.util.Calendar.getInstance;
import static java.util.stream.IntStream.range;

@RequiredArgsConstructor
public class CacheWarmer {
    @Qualifier("cachedTemperatureSource")
    private final TemperatureSource cachedTemperatureSource;

    //Updating cache for current day every midnight
    @Scheduled(cron = "0 0 * * *")
    public void warmCacheForThisDayAtMidnight() {
        long currentHourTimestamp = getInstance().getTimeInMillis() / (1000 * 60 * 60);
        range(0, 24).forEach(hours -> cachedTemperatureSource.get(currentHourTimestamp + hours));
    }
}
