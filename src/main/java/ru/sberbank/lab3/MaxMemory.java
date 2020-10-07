package ru.sberbank.lab3;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.util.function.Function;

public class MaxMemory {

    /**
     * Задание: запускать код этого класса со следующими параметрами VM и
     * объяснить значение Max memory, которое выводится в консоль, для каждого случая.
     * Желательно использовать Java версии 8, если используется другая, то в каждом пункте, кроме пятого, нужно еще добавить параметр -XX:+UseConcMarkSweepGC

     1.  -Xmx512m

     2.  -Xmx512m -Xms512m

     3.  -Xmx512m -Xms512m -XX:+PrintGCDetails

     4.  -Xms512m -Xmx512m -XX:SurvivorRatio=100

     5.  -Xmx512m -XX:+UseG1GC
     */

    public static void main(final String[] args) {
        printMaxMemory();
    }

    public static void printMaxMemory() {
        final long maxMemory = Runtime.getRuntime().maxMemory();

        System.out.printf("Max memory: %d MB", maxMemory / 1024 / 1024);
    }
}