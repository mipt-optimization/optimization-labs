package ru.sberbank.lab3;

public class SecondTask {
    public static void main(String[] args) {//1. Что может сделать JIT-компилятор в этом методе?
        for (int i = 0; i < 50000; i++) {
            Adder adder;
            if (i < 45000) {
                adder = new Simple();
            } else {
                adder = new SimpleWithLogger();
            }
            adder.addARandomNumber(11);
        }
    }
}

interface Adder {
    void addARandomNumber(double value);
}

class Simple implements Adder {
    @Override
    public void addARandomNumber(double value) {
        double random = Math.random();
        double finalResult = random + value;//1. Что может здесь сделать JIT-компилятор?
    }
}

class SimpleWithLogger implements Adder {
    @Override
    public void addARandomNumber(double value) {
        System.out.println("The value is: " + Math.random() + value);
    }
}