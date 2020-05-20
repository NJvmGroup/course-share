public class Demo {
 int a = 0;
 volatile boolean flag = false;

 public int getValue(){
     try {
         Thread.sleep(2000);
     } catch (InterruptedException e) {
         e.printStackTrace();
     }
     return 2;
 }

 public void testA(){
     a = 1;
     flag = true;
 }

 public void testB(){
     while (!flag){

     }
     System.out.println(a+5);
 }

    public static void main(String[] args) {
        final src.Demo demo = new src.Demo();
        new Thread(new Runnable() {
            public void run() {
                demo.testA();
            }
        },"A").start();

        new Thread(new Runnable() {
            public void run() {
                demo.testB();
            }
        },"B").start();
    }
}
