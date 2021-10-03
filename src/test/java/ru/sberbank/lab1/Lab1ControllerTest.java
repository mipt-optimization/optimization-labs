package ru.sberbank.lab1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Random;
import java.util.stream.IntStream;

import static java.time.Duration.ofNanos;
import static java.time.Duration.ofSeconds;
import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.reactive.server.WebTestClient.bindToController;
import static reactor.core.publisher.Mono.just;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = Lab1Controller.class)
@Import({Lab1Controller.class})
class Lab1ControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private DarkskyClient darkskyClient;

    private static final String darkskyResponse = "{\"latitude\":34.053044,\"longitude\":-118.24375,\"timezone\":\"America/Los_Angeles\",\"currently\":{\"time\":1633183635,\"summary\":\"Clear\",\"icon\":\"clear-day\",\"precipIntensity\":0,\"precipProbability\":0,\"temperature\":64.08,\"apparentTemperature\":64.08,\"dewPoint\":37.4,\"humidity\":0.37,\"pressure\":1014.8,\"windSpeed\":1.41,\"windGust\":3.22,\"windBearing\":333,\"cloudCover\":0.19,\"uvIndex\":0,\"visibility\":10,\"ozone\":286.4},\"hourly\":{\"summary\":\"Clear throughout the day.\",\"icon\":\"clear-day\",\"data\":[{\"time\":1633158000,\"summary\":\"Clear\",\"icon\":\"clear-night\",\"precipIntensity\":0,\"precipProbability\":0,\"temperature\":68.92,\"apparentTemperature\":68.92,\"dewPoint\":47.5,\"humidity\":0.46,\"pressure\":1014.5,\"windSpeed\":1.85,\"windGust\":3.78,\"windBearing\":61,\"cloudCover\":0.03,\"uvIndex\":0,\"visibility\":10,\"ozone\":289.1},{\"time\":1633161600,\"summary\":\"Clear\",\"icon\":\"clear-night\",\"precipIntensity\":0,\"precipProbability\":0,\"temperature\":67.48,\"apparentTemperature\":67.48,\"dewPoint\":46.75,\"humidity\":0.47,\"pressure\":1014.6,\"windSpeed\":2.41,\"windGust\":4.28,\"windBearing\":343,\"cloudCover\":0.03,\"uvIndex\":0,\"visibility\":10,\"ozone\":287.9},{\"time\":1633165200,\"summary\":\"Clear\",\"icon\":\"clear-night\",\"precipIntensity\":0,\"precipProbability\":0,\"temperature\":66.58,\"apparentTemperature\":66.58,\"dewPoint\":45.4,\"humidity\":0.46,\"pressure\":1014.5,\"windSpeed\":1.68,\"windGust\":3.71,\"windBearing\":332,\"cloudCover\":0.1,\"uvIndex\":0,\"visibility\":10,\"ozone\":287.1},{\"time\":1633168800,\"summary\":\"Clear\",\"icon\":\"clear-night\",\"precipIntensity\":0,\"precipProbability\":0,\"temperature\":66.02,\"apparentTemperature\":66.02,\"dewPoint\":44.15,\"humidity\":0.45,\"pressure\":1014.3,\"windSpeed\":1.84,\"windGust\":4.06,\"windBearing\":333,\"cloudCover\":0.1,\"uvIndex\":0,\"visibility\":10,\"ozone\":286.3},{\"time\":1633172400,\"summary\":\"Clear\",\"icon\":\"clear-night\",\"precipIntensity\":0,\"precipProbability\":0,\"temperature\":64.81,\"apparentTemperature\":64.81,\"dewPoint\":41.97,\"humidity\":0.43,\"pressure\":1013.9,\"windSpeed\":1.81,\"windGust\":3.69,\"windBearing\":329,\"cloudCover\":0.02,\"uvIndex\":0,\"visibility\":10,\"ozone\":285.8},{\"time\":1633176000,\"summary\":\"Clear\",\"icon\":\"clear-night\",\"precipIntensity\":0,\"precipProbability\":0,\"temperature\":64.68,\"apparentTemperature\":64.68,\"dewPoint\":40.31,\"humidity\":0.41,\"pressure\":1013.9,\"windSpeed\":1.98,\"windGust\":4.43,\"windBearing\":333,\"cloudCover\":0.15,\"uvIndex\":0,\"visibility\":10,\"ozone\":286.5},{\"time\":1633179600,\"summary\":\"Clear\",\"icon\":\"clear-night\",\"precipIntensity\":0,\"precipProbability\":0,\"temperature\":64.24,\"apparentTemperature\":64.24,\"dewPoint\":38.55,\"humidity\":0.39,\"pressure\":1014.3,\"windSpeed\":1.31,\"windGust\":2.97,\"windBearing\":332,\"cloudCover\":0.15,\"uvIndex\":0,\"visibility\":10,\"ozone\":286.3},{\"time\":1633183200,\"summary\":\"Clear\",\"icon\":\"clear-day\",\"precipIntensity\":0,\"precipProbability\":0,\"temperature\":63.86,\"apparentTemperature\":63.86,\"dewPoint\":37.35,\"humidity\":0.37,\"pressure\":1014.8,\"windSpeed\":1.41,\"windGust\":3.17,\"windBearing\":331,\"cloudCover\":0.19,\"uvIndex\":0,\"visibility\":10,\"ozone\":286.4},{\"time\":1633186800,\"summary\":\"Clear\",\"icon\":\"clear-day\",\"precipIntensity\":0,\"precipProbability\":0,\"temperature\":67.48,\"apparentTemperature\":67.48,\"dewPoint\":38.57,\"humidity\":0.35,\"pressure\":1015.1,\"windSpeed\":1.38,\"windGust\":3.62,\"windBearing\":359,\"cloudCover\":0.14,\"uvIndex\":0,\"visibility\":10,\"ozone\":286.5},{\"time\":1633190400,\"summary\":\"Clear\",\"icon\":\"clear-day\",\"precipIntensity\":0,\"precipProbability\":0,\"temperature\":75.66,\"apparentTemperature\":75.66,\"dewPoint\":37.91,\"humidity\":0.26,\"pressure\":1015.1,\"windSpeed\":0.92,\"windGust\":3.3,\"windBearing\":27,\"cloudCover\":0.07,\"uvIndex\":2,\"visibility\":10,\"ozone\":287.9},{\"time\":1633194000,\"summary\":\"Clear\",\"icon\":\"clear-day\",\"precipIntensity\":0,\"precipProbability\":0,\"temperature\":83.59,\"apparentTemperature\":83.59,\"dewPoint\":37.28,\"humidity\":0.19,\"pressure\":1015.2,\"windSpeed\":1.84,\"windGust\":4.63,\"windBearing\":155,\"cloudCover\":0.05,\"uvIndex\":4,\"visibility\":10,\"ozone\":289},{\"time\":1633197600,\"summary\":\"Clear\",\"icon\":\"clear-day\",\"precipIntensity\":0,\"precipProbability\":0,\"temperature\":89.59,\"apparentTemperature\":89.59,\"dewPoint\":34.11,\"humidity\":0.14,\"pressure\":1015.2,\"windSpeed\":1.82,\"windGust\":4.65,\"windBearing\":198,\"cloudCover\":0.04,\"uvIndex\":5,\"visibility\":10,\"ozone\":289.9},{\"time\":1633201200,\"summary\":\"Clear\",\"icon\":\"clear-day\",\"precipIntensity\":0,\"precipProbability\":0,\"temperature\":93,\"apparentTemperature\":93,\"dewPoint\":32.65,\"humidity\":0.12,\"pressure\":1014.9,\"windSpeed\":3.42,\"windGust\":6.92,\"windBearing\":212,\"cloudCover\":0.02,\"uvIndex\":7,\"visibility\":10,\"ozone\":290.2},{\"time\":1633204800,\"summary\":\"Clear\",\"icon\":\"clear-day\",\"precipIntensity\":0,\"precipProbability\":0,\"temperature\":94.9,\"apparentTemperature\":94.9,\"dewPoint\":34.04,\"humidity\":0.12,\"pressure\":1014.5,\"windSpeed\":3.84,\"windGust\":7.95,\"windBearing\":221,\"cloudCover\":0.02,\"uvIndex\":7,\"visibility\":10,\"ozone\":289.7},{\"time\":1633208400,\"summary\":\"Clear\",\"icon\":\"clear-day\",\"precipIntensity\":0,\"precipProbability\":0,\"temperature\":96,\"apparentTemperature\":96,\"dewPoint\":35.91,\"humidity\":0.12,\"pressure\":1013.6,\"windSpeed\":5.43,\"windGust\":9.55,\"windBearing\":234,\"cloudCover\":0.02,\"uvIndex\":6,\"visibility\":10,\"ozone\":288.1},{\"time\":1633212000,\"summary\":\"Clear\",\"icon\":\"clear-day\",\"precipIntensity\":0,\"precipProbability\":0,\"temperature\":95.88,\"apparentTemperature\":95.88,\"dewPoint\":35.44,\"humidity\":0.12,\"pressure\":1012.9,\"windSpeed\":5.59,\"windGust\":9.72,\"windBearing\":253,\"cloudCover\":0.02,\"uvIndex\":4,\"visibility\":10,\"ozone\":287.1},{\"time\":1633215600,\"summary\":\"Clear\",\"icon\":\"clear-day\",\"precipIntensity\":0,\"precipProbability\":0,\"temperature\":93.78,\"apparentTemperature\":93.78,\"dewPoint\":36.41,\"humidity\":0.13,\"pressure\":1012.4,\"windSpeed\":5.87,\"windGust\":10.57,\"windBearing\":243,\"cloudCover\":0.03,\"uvIndex\":2,\"visibility\":10,\"ozone\":287.5},{\"time\":1633219200,\"summary\":\"Clear\",\"icon\":\"clear-day\",\"precipIntensity\":0,\"precipProbability\":0,\"temperature\":91.98,\"apparentTemperature\":91.98,\"dewPoint\":35.36,\"humidity\":0.14,\"pressure\":1012.1,\"windSpeed\":5.48,\"windGust\":10.61,\"windBearing\":240,\"cloudCover\":0.04,\"uvIndex\":1,\"visibility\":10,\"ozone\":287.7},{\"time\":1633222800,\"summary\":\"Clear\",\"icon\":\"clear-day\",\"precipIntensity\":0,\"precipProbability\":0,\"temperature\":87.11,\"apparentTemperature\":87.11,\"dewPoint\":37.48,\"humidity\":0.17,\"pressure\":1012.2,\"windSpeed\":3.85,\"windGust\":9.55,\"windBearing\":230,\"cloudCover\":0.05,\"uvIndex\":0,\"visibility\":10,\"ozone\":288},{\"time\":1633226400,\"summary\":\"Clear\",\"icon\":\"clear-night\",\"precipIntensity\":0,\"precipProbability\":0,\"temperature\":82.21,\"apparentTemperature\":82.21,\"dewPoint\":37.92,\"humidity\":0.21,\"pressure\":1012.6,\"windSpeed\":2.61,\"windGust\":6.95,\"windBearing\":287,\"cloudCover\":0.11,\"uvIndex\":0,\"visibility\":10,\"ozone\":288.5},{\"time\":1633230000,\"summary\":\"Clear\",\"icon\":\"clear-night\",\"precipIntensity\":0,\"precipProbability\":0,\"temperature\":79.4,\"apparentTemperature\":79.4,\"dewPoint\":39.28,\"humidity\":0.24,\"pressure\":1013,\"windSpeed\":2.21,\"windGust\":4.7,\"windBearing\":280,\"cloudCover\":0.14,\"uvIndex\":0,\"visibility\":10,\"ozone\":288.3},{\"time\":1633233600,\"summary\":\"Clear\",\"icon\":\"clear-night\",\"precipIntensity\":0,\"precipProbability\":0,\"temperature\":77.92,\"apparentTemperature\":77.92,\"dewPoint\":38.44,\"humidity\":0.24,\"pressure\":1013.1,\"windSpeed\":1.93,\"windGust\":4.84,\"windBearing\":267,\"cloudCover\":0.06,\"uvIndex\":0,\"visibility\":10,\"ozone\":287.9},{\"time\":1633237200,\"summary\":\"Clear\",\"icon\":\"clear-night\",\"precipIntensity\":0,\"precipProbability\":0,\"temperature\":75.87,\"apparentTemperature\":75.87,\"dewPoint\":38.48,\"humidity\":0.26,\"pressure\":1013.2,\"windSpeed\":2.41,\"windGust\":5.52,\"windBearing\":231,\"cloudCover\":0.12,\"uvIndex\":0,\"visibility\":10,\"ozone\":287.1},{\"time\":1633240800,\"summary\":\"Clear\",\"icon\":\"clear-night\",\"precipIntensity\":0,\"precipProbability\":0,\"temperature\":73.96,\"apparentTemperature\":73.96,\"dewPoint\":39.2,\"humidity\":0.28,\"pressure\":1013.2,\"windSpeed\":2.94,\"windGust\":6.56,\"windBearing\":108,\"cloudCover\":0.09,\"uvIndex\":0,\"visibility\":10,\"ozone\":286.5}]},\"flags\":{\"sources\":[\"cmc\",\"gfs\",\"hrrr\",\"icon\",\"isd\",\"madis\",\"nam\",\"sref\"],\"nearest-station\":3.37,\"units\":\"us\"},\"offset\":-7}\n";
    private static final double baseline = 1.22e7;

    @BeforeEach
    void setUp() {
        when(darkskyClient.get(anyString())).thenReturn(just(darkskyResponse).delayElement(ofSeconds(1)));
    }

    @Test
    void test() {
        var duration = averageDuration(3);
        System.out.println("Average runtime per day (nanos): " + duration);
        System.out.println("Compared to baseline: " + (duration - baseline) + ", relative change: " + (duration - baseline) / baseline);
        verify(darkskyClient, atLeastOnce()).get(anyString());
    }

    private double averageDuration(int nTests) {
        Random random = new Random();
        return range(0, nTests)
                .map(i -> 2 + random.nextInt(2))
                .mapToDouble(days -> testAndMeasure(days).toNanos() * 1.0 / days)
                .average().orElseThrow();
    }

    private Duration testAndMeasure(int days) {
        long start = System.nanoTime();
        webTestClient.get()
                .uri("/lab1/weather?days=" + days)
                .exchange()
                .expectStatus().isOk();
        var res = ofNanos(System.nanoTime() - start);
        System.out.println("Call executed in " + res.toString());
        return res;
    }
}