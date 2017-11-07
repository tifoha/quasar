package ua.tifoha.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.io.FiberServerSocketChannel;
import co.paralleluniverse.fibers.io.FiberSocketChannel;

/**
 * Created by Vitaliy Sereda on 02.10.17.
 */
public class FiberHttpServer {
	//	public static void main(String[] args) throws Exception {
//		new Fiber(() -> {
//			try {
//				System.out.println("Starting server.");
//				FiberServerSocketChannel sockServer = FiberServerSocketChannel.open().bind(new InetSocketAddress(8080));
//				for (; ; ) {
//					FiberSocketChannel sock = sockServer.accept();
//					new Fiber(() -> {
//						System.out.println("Handing over request to new fiber");
//						try {
//							ByteBuffer buf = ByteBuffer.allocateDirect(1024);
//							int n = sock.read(buf);
////							String response = "HTTP/1.0 200 OK\r\nDate: Fri, 31 Dec 1999 23:59:59 GMT\r\n" + "Content-Type: text/html\r\nContent-Length: 1\r\n\r\nx";
//							final String content = "<html>" +
//									"<body>" +
//									"<h1>Hello, World!</h1>" +
//									"</body>" +
//									"</html>";
//							String response = "HTTP/1.1 200 OK\r\n" +
//									"Date: Mon, 27 Jul 2009 12:28:53 GMT\r\n" +
//									"Server: Apache/2.2.14 (Win32)\r\n" +
//									"Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT\r\n" +
//									"Content-Type: text/html\r\n" +
//									"Connection: Closed\r\n" +
//									"Content-Length: " + content.getBytes(StandardCharsets.UTF_8).length + "\r\n\r\n" +
//									content;
//							sock.write(ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8)));
//							sock.close();
//						} catch (IOException ioe) {
//							ioe.printStackTrace();
//						}
//					}
//					).start();
//				}
//			} catch (IOException ioe) {
//				ioe.printStackTrace();
//			}
//		}
//		).start();
//		System.out.println("Socket server started...");
//		Thread.sleep(Long.MAX_VALUE);
//	}
	public static void main(String[] args) throws ExecutionException, InterruptedException {
		final Fiber<Void> fiber = new Fiber<Void>() {
			@Override
			protected Void run() throws SuspendExecution, InterruptedException {

				System.out.println("Starting server.");
				try (FiberServerSocketChannel sockServer = FiberServerSocketChannel.open().bind(new InetSocketAddress(8080))) {
					while (!Fiber.currentFiber().isInterrupted()) {
						FiberSocketChannel sock = sockServer.accept();
						new Fiber(() -> {
							System.out.println("Handing over request to new fiber");
							try {
								ByteBuffer buf = ByteBuffer.allocateDirect(1024);
								int n = sock.read(buf);
//							String response = "HTTP/1.0 200 OK\r\nDate: Fri, 31 Dec 1999 23:59:59 GMT\r\n" + "Content-Type: text/html\r\nContent-Length: 1\r\n\r\nx";
								final String content = "<html>" +
										"<body>" +
										"<h1>Hello, World!</h1>" +
										"</body>" +
										"</html>";
								String response = "HTTP/1.1 200 OK\r\n" +
										"Date: Mon, 27 Jul 2009 12:28:53 GMT\r\n" +
										"Server: Apache/2.2.14 (Win32)\r\n" +
										"Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT\r\n" +
										"Content-Type: text/html\r\n" +
										"Connection: Closed\r\n" +
										"Content-Length: " + content.getBytes(StandardCharsets.UTF_8).length + "\r\n\r\n" +
										content;
								sock.write(ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8)));
								sock.close();
							} catch (IOException ioe) {
								ioe.printStackTrace();
							}
						}
						).start();
					}
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
				return null;
			}
		};
		fiber.start();
		fiber.join();
	}
}
