import java.util.ArrayList;
import java.util.List;

public class Container {
    public static class SomeThing {
        private int status;

        public SomeThing() {
            status = 1;
        }

        public int getStatus() {
            return status;
        }
    }

    private volatile  SomeThing object;

    public void create() {
        object = new SomeThing();
    }

    public SomeThing get() {
        while (object == null) {
            Thread.yield(); //不加这句话可能会在此出现无限循环
        }
        return object;
    }




    public static void main(String[] args) throws InterruptedException {
        final int THREADS_COUNT = 20;
        final int LOOP_COUNT = 100000;

        long sum = 0;
        long min = Integer.MAX_VALUE;
        long max = 0;
        for(int n = 0;n <= 100;n++) {
            final Container basket = new Container();
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