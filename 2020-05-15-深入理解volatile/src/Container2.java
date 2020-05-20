package src;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Container2 {
    public static class SomeThing {
        private int status;

        public SomeThing() {
            status = 1;
        }

        public int getStatus() {
            return status;
        }
    }

    private SomeThing object;

    private Object value;
    private static final Unsafe unsafe = getUnsafe();
    private static final long valueOffset;
    static {
        try {
            valueOffset = unsafe.objectFieldOffset(Container2.class.getDeclaredField("value"));
        } catch (Exception ex) { throw new Error(ex); }
    }

    public void create() {
        SomeThing temp = new SomeThing();
        unsafe.putOrderedObject(this, valueOffset, null);	//将value赋null值只是一项无用操作，实际利用的是这条语句的内存屏障
        object = temp;
    }

    public SomeThing get() {
        while (object == null) {
            Thread.yield();
        }
        return object;
    }


    public static Unsafe getUnsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe)f.get(null);
        } catch (Exception e) {
        }
        return null;
    }


    public static void main(String[] args) throws InterruptedException {
        final int THREADS_COUNT = 20;
        final int LOOP_COUNT = 100000;

        long sum = 0;
        long min = Integer.MAX_VALUE;
        long max = 0;
        for(int n = 0;n <= 100;n++) {
            final Container2 basket = new Container2();
            List<Thread> putThreads = new ArrayList<Thread>();
            List<Thread> takeThreads = new ArrayList<Thread>();
            for (int i = 0; i < THREADS_COUNT; i++) {
                putThreads.add(new Thread() {
                    @Override
                    public void run() {
                        for (int j = 0; j < LOOP_COUNT; j++) {
                            basket.create();
                        }
                    }
                });
                takeThreads.add(new Thread() {
                    @Override
                    public void run() {
                        for (int j = 0; j < LOOP_COUNT; j++) {
                            basket.get().getStatus();
                        }
                    }
                });
            }
            long start = System.nanoTime();
            for (int i = 0; i < THREADS_COUNT; i++) {
                takeThreads.get(i).start();
                putThreads.get(i).start();
            }
            for (int i = 0; i < THREADS_COUNT; i++) {
                takeThreads.get(i).join();
                putThreads.get(i).join();
            }
            long end = System.nanoTime();
            long period = end - start;
            if(n == 0) {
                continue;	//由于JIT的编译，第一次执行需要更多时间，将此时间不计入统计
            }
            sum += (period);
            System.out.println(period);
            if(period < min) {
                min = period;
            }
            if(period > max) {
                max = period;
            }
        }
        System.out.println("Average : " + sum / 100);
        System.out.println("Max : " + max);
        System.out.println("Min : " + min);
    }
}