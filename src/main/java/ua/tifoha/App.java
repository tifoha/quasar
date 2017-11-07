package ua.tifoha;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.concurrent.ExecutionException;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.FiberForkJoinScheduler;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.io.FiberSocketChannel;
import co.paralleluniverse.strands.SuspendableRunnable;

/**
 * Hello world!
 */
public class App {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
//		System.out.println("Hello World!");
////        ExecutorService exec = Executors.newFixedThreadPool(1);
//		long start = System.currentTimeMillis();
//        int count = 6;
//        for (int i = 0; i < count; i++) {
//			new Fiber<>("Fiber" + i, () -> {
//				print();
//				Fiber.sleep(500, TimeUnit.MILLISECONDS);
//				print();
//				Fiber.sleep(500, TimeUnit.MILLISECONDS);
//			}).start();
////            exec.submit(() -> {
////                TimeUnit.MILLISECONDS.sleep(500);
////                System.out.println(Fiber.currentFiber());
////                return null;
////            });
//        }
////        exec.shutdown();
////        exec.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
////		new Fiber<>("Fiber" + 1, () -> {
////			print();
////			Fiber.sleep(500, TimeUnit.MILLISECONDS);
////			print();
////		}).start();
////		voidFiber.join();
//		System.out.println(Duration.ofMillis(System.currentTimeMillis() - start));
//		System.out.println("End.");
		int PORT = 1234;
		Charset charset = Charset.forName("UTF-8");
		CharsetEncoder encoder = charset.newEncoder();
		CharsetDecoder decoder = charset.newDecoder();
		FiberForkJoinScheduler scheduler = new FiberForkJoinScheduler("test", 4, null, false);
		for (int i = 0; i < 500; i++) {

			final Fiber client = new Fiber(scheduler, new SuspendableRunnable() {
				@Override
				public void run() throws SuspendExecution {
//				try {
////					sync.receive(); // Wait that the server is ready
//				} catch (InterruptedException ex) {
//					// This should never happen
//					throw new AssertionError(ex);
//				}

					try (FiberSocketChannel ch = FiberSocketChannel.open(new InetSocketAddress(PORT))) {
						ByteBuffer buf = ByteBuffer.allocateDirect(1024);
						String req2 = "my request";
						ch.write(encoder.encode(CharBuffer.wrap(req2)));

						buf.clear();
						ch.read(buf); // we assume the message is sent in a single packet

						buf.flip();
						String res2 = decoder.decode(buf).toString();
//					System.out.println(res2);

						// verify that the server has closed the socket
						buf.clear();
						int n = ch.read(buf);

//					assertThat(n, is(-1));
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}).start();
		}

//		client.join();
	}

	public static void print() {
		System.out.printf("%s:%s%n", Fiber.currentFiber().getName(), Thread.currentThread().getName());
	}
}
