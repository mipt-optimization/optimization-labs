package ru.sberbank.lab1;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

import static java.lang.String.valueOf;
import static ru.sberbank.utils.Utils.getZonedDateTimeAtStartOfHour;

@Service
@RequiredArgsConstructor
public class CacheWarmer {
    private final TemperatureProvider temperatureProvider;
    private final static int WARM_UP_DEPTH_HOURS = 10 * 24;

    @Scheduled(cron = "${cache.warmup.cron}")
    public void warmUpCache() {
        ZonedDateTime zonedDateTime = getZonedDateTimeAtStartOfHour();

        for (int i = 0; i < WARM_UP_DEPTH_HOURS; i++) {
            long currentHourSeconds = zonedDateTime.minusHours(i).toEpochSecond();
            temperatureProvider.getTemperature(valueOf(currentHourSeconds));
        }
    }
}