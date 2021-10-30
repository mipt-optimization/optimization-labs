package ru.sberbank.lab2;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Arrays;

public class PhantomReferences {
    /**
     * Задание: запускать код этого класса со следующими параметрами VM, понадлюдать сколько было сборок на протяжении 15-30 секунд и объяснить результаты.
     * Желательно использовать Java версии 8, если используется другая, то в каждом пункте, нужно еще добавить параметр -XX:+UseConcMarkSweepGC
     * <p>
     * 1.  -verbose:gc -Xmx24m -XX:NewSize=16m -XX:MaxTenuringThreshold=1 -XX:-UseAdaptiveSizePolicy
     * <p>
     * 2.  -Dphantom.refs=true -verbose:gc -Xmx24m -XX:NewSize=16m -XX:MaxTenuringThreshold=1 -XX:-UseAdaptiveSizePolicy
     * <p>
     * 3.  -Dphantom.refs=true -verbose:gc -Xmx64m -XX:NewSize=32m -XX:MaxTenuringThreshold=1 -XX:-UseAdaptiveSizePolicy
     * <p>
     * 4.  -Dphantom.refs=true -Dno.ref.clearing=true -verbose:gc -Xmx64m -XX:NewSize=32m -XX:MaxTenuringThreshold=1 -XX:-UseAdaptiveSizePolicy
     */

    private static final int OBJECT_SIZE = 192;
    private static final int BUFFER_SIZE = 24 * 1024;
    private static final boolean PHANTOM_REFS_FOR_ALL = Boolean.getBoolean("phantom.refs");
    private static final boolean CLEAR_REFS = !Boolean.getBoolean("no.ref.clearing");

    public static volatile Object sink;

    private static final ReferenceQueue<Object> queue = new ReferenceQueue<>();

    public static void main(String[] args) {
        final Object substitute = makeObject();
        final Object[] refs = new Object[BUFFER_SIZE];

        System.gc();

        for (int index = 0; ; ) {
            Object object = makeObject();
            sink = object;

            if (!PHANTOM_REFS_FOR_ALL) {
                object = substitute;
            }

            refs[index++] = new PhantomReference<>(object, queue);

            if (index == BUFFER_SIZE) {
                Arrays.fill(refs, null);
                index = 0;
            }

            if (CLEAR_REFS) {
                Reference ref;
                while ((ref = queue.poll()) != null) {
                    ref.clear();
                }
            }
        }
    }

    private static Object makeObject() {
        return new byte[OBJECT_SIZE];
    }
}