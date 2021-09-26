package ru.mipt;

import org.junit.Test;
import org.mockito.Mockito;
import ru.sberbank.lab1.Lab1Controller;
import ru.sberbank.lab1.MyClass;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class Lab1ControllerTest {

    @Test
    public void mustReturnWeatherOverPeriod() {
        Lab1Controller controller = new Lab1Controller();
        int days = 2;

        List<Double> temperatures = controller.getWeatherForPeriod(days);

        assertEquals(temperatures.get(0), (Double)62.0);
        assertEquals(temperatures.get(1), (Double)64.05);

    }

}
