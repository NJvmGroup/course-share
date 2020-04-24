import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReOrder {
    private int x = 10;
    private boolean ready = false;

    public int read() {
        int y = 0;

        if (ready) {
            y = x;
        }
        if (y == 10) {
            System.out.println(y);
        }
        return y;
    }

    public void  write() {
        x = 8;
        ready = true;
    }

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        while (true) {
            ReOrder reorder = new ReOrder();
            executor.submit(() -> reorder.write());
            executor.submit(() -> reorder.read());

        }

    }
}
