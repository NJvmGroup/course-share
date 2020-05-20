package src;

public class CacheLine2 {
    public static  long[] arr = new long[16];

    public static void main(String[] args) throws InterruptedException {

        long sum = 0;
        long min = Integer.MAX_VALUE;
        long max = 0;
        for(int n = 0;n <= 100;n++) {
            Thread t1 = new Thread(()->{
                for(long i=0; i<1000000000L; i++){
                    arr[0] = i;
                }
            });
            Thread t2 = new Thread(()->{
                for(long i=0; i<1000000000L; i++){
                    arr[8] = i;
                }
            });

            long start = System.nanoTime();
            t1.start();
            t2.start();
//            t1.join();
//            t2.join();
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
