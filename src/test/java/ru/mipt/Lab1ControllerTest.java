package ru.mipt;

import org.junit.Test;
import ru.sberbank.lab1.Lab1Controller;
import java.util.List;
import static org.junit.Assert.assertEquals;


public class Lab1ControllerTest {

    @Test
    public void mustReturnWeatherOverPeriod() {
        Lab1Controller controller = new Lab1Controller();
        int days = 2;

        List<Double> temperatures = controller.getWeatherForPeriod(days);

        assertEquals(temperatures.get(0), (Double)60.85);
        assertEquals(temperatures.get(1), (Double)62.0);

    }

}
