package ru.sberbank.lab3;

import java.util.Random;

public class FirstTask {
    public long outer(int i) {
        long sum = 0;

        for (int j = 0; j < 20_000; j++) {
            sum += inner(i, j);//1. Какую оптимизацию может здесь применить JIT-компилятор
        }

        return sum;
    }

    public long inner(int i, int j) {
        long sum = 0;

        int[] parts = new int[2];

        parts[0] = i;
        parts[1] = j;

        Random random = new Random();

        if (random.nextBoolean()) {
            sum += parts[0];
        } else {
            sum += parts[1];
        }

        return sum;//2. Зачем мы возвращаем отсюда значение?
    }

    public static void main(final String[] args) {
        FirstTask test = new FirstTask();

        long sum = 0;

        for (int i = 0; i < 20_000; i++) {
            sum += test.outer(i);
        }

        System.out.println(sum);
    }
}