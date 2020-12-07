package ru.sberbank.demo4;

import java.lang.ref.SoftReference;

public class SoftRefExample {
    public static void main(String[] args) {
        Employee obj = new Employee();
        SoftReference sf = new SoftReference<>(obj);
        obj = null;
        System.gc();
        byte[] bytes = new byte[1024 * 511];
        System.gc();
        System.out.println(sf.get());
    }
}