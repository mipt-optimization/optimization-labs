package ru.sberbank.demo4;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.List;

public class PhantomRefExample {
    public static void main(String[] args) throws InterruptedException {
        ReferenceQueue<Integer> queue = new ReferenceQueue<>();
        List<PhantomInteger> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Integer x = new Integer(i);
            list.add(new PhantomInteger(x, queue));
        }

        System.gc();
        Thread.sleep(10L);
        Reference<? extends Integer> referenceFromQueue;
        while ((referenceFromQueue = queue.poll()) != null) {
            //System.out.println(referenceFromQueue.get());
            //referenceFromQueue.clear();
        }
    }

    static class PhantomInteger extends PhantomReference<Integer> {

        /**
         * Creates a new phantom reference that refers to the given object and
         * is registered with the given queue.
         *
         * <p> It is possible to create a phantom reference with a <tt>null</tt>
         * queue, but such a reference is completely useless: Its <tt>get</tt>
         * method will always return null and, since it does not have a queue, it
         * will never be enqueued.
         *
         * @param referent the object the new phantom reference will refer to
         * @param q        the queue with which the reference is to be registered,
         */
        public PhantomInteger(Integer referent, ReferenceQueue<? super Integer> q) {
            super(referent, q);
        }

        @Override
        public void clear() {
            System.out.println("fin");
        }
    }
}
