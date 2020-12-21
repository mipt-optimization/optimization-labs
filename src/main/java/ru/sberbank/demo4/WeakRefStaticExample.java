package ru.sberbank.demo4;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class WeakRefStaticExample {
    private static List<WeakReference<byte[]>> byteList = new ArrayList<>();

    public static void main(String[] args) {
        while (true) {
            WeakReference<byte[]> sf = new WeakReference(new byte[1024]);
            byteList.add(sf);
        }
    }
}
