package ua.tifoha;

import static ua.tifoha.App.print;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.FiberExecutorScheduler;
import co.paralleluniverse.fibers.FiberSchedulerTask;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.SuspendableRunnable;

/**
 * Created by Vitaliy Sereda on 24.09.17.
 */
public class SchedulerTests {
	public static void main(String[] args) {
		ExecutorService exec = Executors.newFixedThreadPool(1);
		FiberExecutorScheduler fExec = new FiberExecutorScheduler("FiberExecutor", exec);
		int count = 10;
		for (int i = 0; i < count; i++) {
			fExec.execute(() -> {
//				print();
//				System.out.println(Fiber.currentFiber());
//				try {
//					TimeUnit.MILLISECONDS.sleep(500);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
				try {
					Fiber.sleep(500, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (SuspendExecution suspendExecution) {
					suspendExecution.printStackTrace();
				}
				System.out.println("XXX");
			});

		}
		exec.shutdown();
	}
}

class MyTask implements FiberSchedulerTask ,SuspendableRunnable{

	@Override
	public Fiber<?> getFiber() {
		return new Fiber<>(this);
	}

	@Override
	public void run() throws SuspendExecution, InterruptedException {

	}
}