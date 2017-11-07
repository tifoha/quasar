package ua.tifoha;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.FiberForkJoinScheduler;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.io.FiberServerSocketChannel;
import co.paralleluniverse.fibers.io.FiberSocketChannel;
import co.paralleluniverse.strands.SuspendableRunnable;

/**
 * Hello world!
 */
public class Server {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		String response = "HTTP/1.0 200 OK\r\nDate: Fri, 31 Dec 1999 23:59:59 GMT\r\nContent-Type: text/html\r\nContent-Length: 2\r\n\r\nOK";
		byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
		int PORT = 1234;
		Charset charset = Charset.forName("UTF-8");
		CharsetEncoder encoder = charset.newEncoder();
		CharsetDecoder decoder = charset.newDecoder();
		FiberForkJoinScheduler scheduler = new FiberForkJoinScheduler("test", 8, null, false);

		final Fiber server = new Fiber(scheduler, new SuspendableRunnable() {
			@Override
			public void run() throws SuspendExecution, InterruptedException {
				try (FiberServerSocketChannel socket = FiberServerSocketChannel.open().bind(new InetSocketAddress(PORT))) {
					while (true) {
						FiberSocketChannel ch = socket.accept();
						new Fiber<Void>(scheduler, new SuspendableRunnable(){
							@Override
							public void run() throws SuspendExecution, InterruptedException {
								ByteBuffer buf = ByteBuffer.allocateDirect(1024);
								try {
									ch.read(buf); // we assume the message is sent in a single packet
									buf.flip();
//									String request = decoder.decode(buf).toString();
//									System.out.println(req2);
									ch.write(ByteBuffer.wrap(responseBytes));
//									System.out.println(request);
									ch.close();
								} catch (IOException e) {
									throw new RuntimeException(e);
								}

							}
						}).start();
					}


				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}).start();
		server.join();
	}
}
