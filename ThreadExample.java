import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadExample {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();

        // Define your tasks just once
        Runnable task1 = new Task1();
        Runnable task2 = new Task2();
        Runnable task3 = new Task3();

        // Execute the first task and wait for it to finish
        try {
            executor.submit(task1).get();
        } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Execute the second and third tasks in parallel
        executor.submit(task2);
        executor.submit(task3);

        // Wait for all tasks to finish. This is a simplistic way to wait,
        // and in a real-world scenario, you might want to use Futures and call .get()
        // on them
        // to ensure they have finished. For simplicity and direct replacement of
        // join(),
        // we'll use a fixed delay. This is NOT the best practice for production code.
 // Wait for tasks to finish

        // Reuse the executor to run the first task again
        executor = Executors.newCachedThreadPool();
        executor.submit(task1);

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES); // Wait for the task to finish
    }

    static class Task1 implements Runnable {
        @Override
        public void run() {
            System.out.println("Executing Task1");
            // Task1 logic here
        }
    }

    static class Task2 implements Runnable {
        @Override
        public void run() {
            System.out.println("Executing Task2");
            // Task2 logic here
        }
    }

    static class Task3 implements Runnable {
        @Override
        public void run() {
            System.out.println("Executing Task3");
            // Task3 logic here
        }
    }
}
