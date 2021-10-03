package ru.sberbank.lab1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;
import java.util.Random;
import java.util.stream.IntStream;

import static java.time.Duration.ofNanos;
import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.reactive.server.WebTestClient.bindToController;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = Lab1Controller.class)
@Import({Lab1Controller.class, WebClientConfig.class})
class Lab1ControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        //webTestClient = bindToController(lab1Controller).build();
    }

    @Test
    void test() {
        System.out.println("Average runtime per day (nanos): " + averageDuration(3));
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