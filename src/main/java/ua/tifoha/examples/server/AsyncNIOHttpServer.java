package ua.tifoha.examples.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Function;
import java.util.function.Supplier;

import ua.tifoha.examples.Utils;

/**
 * Created by Vitaliy Sereda on 05.11.17.
 */
public class AsyncNIOHttpServer {
	private static final Supplier<ExecutorService> EXECUTOR_SERVICE_SUPPLIER = Executors::newCachedThreadPool;
	public static final Function<SocketChannel, Queue<ByteBuffer>> QUEUE_FACTORY = socketChannel -> new LinkedBlockingDeque<>(100);

	private volatile boolean finished = false;
	private volatile int maxConnectionCount = 2000;
	private final InetSocketAddress address;
	private final Selector selector;
	private final ServerSocketChannel ssc;
	private ExecutorService exec;
	private static Map<SocketChannel, Queue<ByteBuffer>> pendingData = new ConcurrentHashMap<>();



	public AsyncNIOHttpServer(InetSocketAddress address) throws IOException {
		this.address = Objects.requireNonNull(address);
		selector = Selector.open();

		ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);
		ssc.register(selector, SelectionKey.OP_ACCEPT);
	}

//	private static Selector selector;
//	private static Map<SocketChannel, Queue<ByteBuffer>> pendingData = new ConcurrentHashMap<>();
//
//	public static void main(String[] args) throws IOException {
//		ServerSocketChannel ssc = ServerSocketChannel.open();
//		ssc.bind(new InetSocketAddress(8080));
//		ssc.configureBlocking(false);
//		selector = Selector.open();
//		ssc.register(selector, SelectionKey.OP_ACCEPT);
//		while (true) {
//			selector.select();
//			for (Iterator<SelectionKey> itKeys = selector.selectedKeys().iterator(); itKeys.hasNext(); ) {
//				SelectionKey key = itKeys.next();
//				itKeys.remove();
//				if (!key.isValid()) {
//					continue;
//				}
//				if (key.isAcceptable()) {
//					accept(key);
//				} else if (key.isReadable()) {
//					read(key);
//				} else if (key.isWritable()) {
//					write(key);
//				}
//			}
//		}
//	}
//
//	private static void write(SelectionKey key) throws IOException {
//		SocketChannel sc = (SocketChannel) key.channel();
//		if (!pendingData.containsKey(sc)) {
//			return;
//		}
//
//		Queue<ByteBuffer> dataQueue = pendingData.get(sc);
//
//		ByteBuffer buf;
//		while ((buf = dataQueue.peek()) != null) {
//			sc.write(buf);
//			if (!buf.hasRemaining()) {
//				dataQueue.poll();
//			} else {
//				return;
//			}
//			sc.register(selector, SelectionKey.OP_READ);
//		}
//
//	}

	private void read(SelectionKey key) throws IOException {
		SocketChannel sc = (SocketChannel) key.channel();
		ByteBuffer buf = ByteBuffer.allocateDirect(16);
		if (sc.read(buf) == -1) {
			pendingData.remove(sc);
			return;
		}
		buf.flip();
		for (int i = 0; i < buf.limit(); i++) {
			buf.put(i, (byte) Utils.transmografy(buf.get(i)));
		}
		sc.register(selector, SelectionKey.OP_WRITE);
		pendingData.computeIfAbsent(sc, QUEUE_FACTORY).add(buf);
	}

	private void accept(SelectionKey key) throws IOException {
		final ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
		final SocketChannel sc = ssc.accept();

		if (pendingData.size() > maxConnectionCount) {
			rejectConnection(sc);
			return;
		}

		registerConnection(sc);
	}

	private void registerConnection(SocketChannel sc) throws IOException {
		sc.configureBlocking(false);
		sc.register(selector, SelectionKey.OP_READ);
		pendingData.computeIfAbsent(sc, QUEUE_FACTORY);
	}

	private void rejectConnection(SocketChannel sc) throws IOException {
		sc.close();
	}

	public static void main(String[] args) throws IOException {
		AsyncNIOHttpServer server = new AsyncNIOHttpServer(
				new InetSocketAddress(8080)
		);

		server.start();
	}

	public void start() throws IOException {
		if (!finished) {
			if (exec == null) {
				exec = EXECUTOR_SERVICE_SUPPLIER.get();
			}

			exec.submit(this::dispatchConnections);
		} else {
			throw new IllegalStateException("Server is already started");
		}

	}

	private Object dispatchConnections() throws IOException {
		try {
			ssc.bind(address);
			while (!finished) {
				selector.select(500);
				for (Iterator<SelectionKey> itKeys = selector.selectedKeys().iterator(); itKeys.hasNext(); ) {
					SelectionKey key = itKeys.next();
					itKeys.remove();
					if (!key.isValid()) {
						continue;
					}
					if (key.isAcceptable()) {
						accept(key);
					} else if (key.isReadable()) {
						read(key);
					} else if (key.isWritable()) {
//						write(key);
					}
				}
			}
		} finally {
			ssc.close();
		}

		return null;
	}

//	public static class Connection {
//		private final SocketChannel sc;
//
//		public Connection(SocketChannel sc) {
//			this.sc = Objects.requireNonNull(sc);
//		}
//
//		@Override
//		public boolean equals(Object o) {
//			if (this == o) return true;
//			if (!(o instanceof Connection)) return false;
//
//			Connection that = (Connection) o;
//
//			return sc.equals(that.sc);
//		}
//
//		@Override
//		public int hashCode() {
//			return sc.hashCode();
//		}
//	}



}
