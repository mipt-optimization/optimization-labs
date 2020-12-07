package ru.sberbank.demo4;

import java.lang.ref.WeakReference;

public class WeakRefExample {
    public static void main(String[] args) {
        Employee obj = new Employee();
        WeakReference<Employee> sf = new WeakReference<>(obj);
        obj = null;
        System.out.println("Is Collected: " + sf.get());
        System.gc();
        System.out.println("Is Collected: " + sf.get());
    }
}
