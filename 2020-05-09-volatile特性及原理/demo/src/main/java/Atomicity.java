public class Atomicity {
    public static volatile int race = 0;

    public static void main(String[] args) {
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                long val = 0;
                while (val < 10000000L) {
                    race++;
                    val++;
                }
            }
        });
        t1.start();
        Thread t2 = new Thread(new Runnable() {
            public void run() {
                long val = 0;
                while (val < 10000000L) {
                    race++;
                    val++;
                }
            }
        });
        t2.start();

        while (t1.isAlive() || t2.isAlive()) {
        }

        System.out.println("final val is: " + race);
    }
}
