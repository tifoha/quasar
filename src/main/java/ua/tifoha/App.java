package ua.tifoha;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import co.paralleluniverse.fibers.Fiber;

/**
 * Hello world!
 */
public class App {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		System.out.println("Hello World!");
//        ExecutorService exec = Executors.newFixedThreadPool(1);
		long start = System.currentTimeMillis();
        int count = 6;
        for (int i = 0; i < count; i++) {
			new Fiber<>("Fiber" + i, () -> {
				print();
				Fiber.sleep(500, TimeUnit.MILLISECONDS);
				print();
			}).start();
//            exec.submit(() -> {
//                TimeUnit.MILLISECONDS.sleep(500);
//                System.out.println(Fiber.currentFiber());
//                return null;
//            });
        }
//        exec.shutdown();
//        exec.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
//		new Fiber<>("Fiber" + 1, () -> {
//			print();
//			Fiber.sleep(500, TimeUnit.MILLISECONDS);
//			print();
//		}).start();
//		voidFiber.join();
		System.out.println(Duration.ofMillis(System.currentTimeMillis() - start));
		System.out.println("End.");
	}

	public static void print() {
		System.out.printf("%s:%s%n", Fiber.currentFiber().getName(), Thread.currentThread().getName());
	}
}
