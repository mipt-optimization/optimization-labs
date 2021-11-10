package ru.sberbank.lab2;

import java.lang.ref.WeakReference;
import java.util.Arrays;

public class WeakReferences {

    /**
     * Задание: запускать код этого класса со следующими параметрами VM, понадлюдать сколько было сборок на протяжении 15-30 секунд и объяснить результаты.
     * Желательно использовать Java версии 8, если используется другая, то в каждом пункте, нужно еще добавить параметр -XX:+UseConcMarkSweepGC
     * <p>
     * 1.  -verbose:gc -Xmx24m -XX:NewSize=16m -XX:MaxTenuringThreshold=1 -XX:-UseAdaptiveSizePolicy
     * <p>
     * 2.  -Dweak.refs=true -verbose:gc -Xmx24m -XX:NewSize=16m -XX:MaxTenuringThreshold=1 -XX:-UseAdaptiveSizePolicy
     * <p>
     * 3.  -Dweak.refs=true -verbose:gc -Xmx64m -XX:NewSize=32m -XX:MaxTenuringThreshold=1 -XX:-UseAdaptiveSizePolicy
     */

    private static final int OBJECT_SIZE  = 192;
    private static final int BUFFER_SIZE  = 64 * 1024;
    private static final boolean WEAK_REFS_FOR_ALL = Boolean.getBoolean("weak.refs");

    private static Object makeObject() {
        return new byte[OBJECT_SIZE];
    }

    public static volatile Object sink;

    public static void main(String[] args) throws InterruptedException {

        System.out.printf("Buffer size: %d; Object size: %d; Weak refs for all: %s%n", BUFFER_SIZE, OBJECT_SIZE, WEAK_REFS_FOR_ALL);

        final Object substitute = makeObject();
        final Object[] refs = new Object[BUFFER_SIZE];

        System.gc();

        for (int index = 0;;) {
            Object object = makeObject();
            sink = object;

            if (!WEAK_REFS_FOR_ALL) {
                object = substitute;
            }

            refs[index++] = new WeakReference<>(object);

            if (index == BUFFER_SIZE) {
                Arrays.fill(refs, null);
                index = 0;
            }
        }
    }
}