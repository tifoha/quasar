package ua.tifoha;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;

import co.paralleluniverse.common.test.TestUtil;
import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.FiberForkJoinScheduler;
import co.paralleluniverse.fibers.FiberScheduler;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.io.FiberFileChannel;
import co.paralleluniverse.fibers.io.FiberServerSocketChannel;
import co.paralleluniverse.fibers.io.FiberSocketChannel;
import co.paralleluniverse.strands.SuspendableRunnable;
import co.paralleluniverse.strands.channels.Channels;
import co.paralleluniverse.strands.channels.IntChannel;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;

/**
 * Unit test for simple App.
 */

/**
 *
 * @author pron
 */
public class FiberAsyncIOTest {
    @Rule
    public TestName name = new TestName();
    @Rule
    public TestRule watchman = TestUtil.WATCHMAN;

    private static final int PORT = 1234;
    private static final Charset charset = Charset.forName("UTF-8");
    private static final CharsetEncoder encoder = charset.newEncoder();
    private static final CharsetDecoder decoder = charset.newDecoder();
    private final FiberScheduler scheduler;

    public FiberAsyncIOTest() {
        scheduler = new FiberForkJoinScheduler("test", 4, null, false);
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @org.junit.Test
    public void testFiberAsyncSocket() throws Exception {
        final IntChannel sync = Channels.newIntChannel(0);

        final Fiber server = new Fiber(scheduler, new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution, InterruptedException {
                try (FiberServerSocketChannel socket = FiberServerSocketChannel.open().bind(new InetSocketAddress(PORT))) {
                    sync.send(0); // Start client

                    try (FiberSocketChannel ch = socket.accept()) {

                        ByteBuffer buf = ByteBuffer.allocateDirect(1024);

                        // long-typed reqeust/response
                        int n = ch.read(buf);

                        assertThat(n, is(8)); // we assume the message is sent in a single packet

                        buf.flip();
                        long req = buf.getLong();

                        assertThat(req, is(12345678L));

                        buf.clear();
                        long res = 87654321L;
                        buf.putLong(res);
                        buf.flip();

                        n = ch.write(buf);

                        assertThat(n, is(8));

                        // String reqeust/response
                        buf.clear();
                        ch.read(buf); // we assume the message is sent in a single packet

                        buf.flip();
                        String req2 = decoder.decode(buf).toString();

                        assertThat(req2, is("my request"));

                        String res2 = "my response";
                        ch.write(encoder.encode(CharBuffer.wrap(res2)));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        final Fiber client = new Fiber(scheduler, new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution {
                try {
                    sync.receive(); // Wait that the server is ready
                } catch (InterruptedException ex) {
                    // This should never happen
                    throw new AssertionError(ex);
                }

                try (FiberSocketChannel ch = FiberSocketChannel.open(new InetSocketAddress(PORT))) {
                    ByteBuffer buf = ByteBuffer.allocateDirect(1024);

                    // long-typed reqeust/response
                    long req = 12345678L;
                    buf.putLong(req);
                    buf.flip();

                    int n = ch.write(buf);

                    assertThat(n, is(8));

                    buf.clear();
                    n = ch.read(buf);

                    assertThat(n, is(8)); // we assume the message is sent in a single packet

                    buf.flip();
                    long res = buf.getLong();

                    assertThat(res, is(87654321L));

                    // String reqeust/response
                    String req2 = "my request";
                    ch.write(encoder.encode(CharBuffer.wrap(req2)));

                    buf.clear();
                    ch.read(buf); // we assume the message is sent in a single packet

                    buf.flip();
                    String res2 = decoder.decode(buf).toString();

                    assertThat(res2, is("my response"));

                    // verify that the server has closed the socket
                    buf.clear();
                    n = ch.read(buf);

                    assertThat(n, is(-1));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        client.join();
        server.join();
    }

    @Test
    public void testFiberAsyncFile() throws Exception {
        new Fiber(scheduler, new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution {
                try (FiberFileChannel ch = FiberFileChannel.open(Paths.get(System.getProperty("user.home"), "fibertest.bin"), READ, WRITE, CREATE, TRUNCATE_EXISTING)) {
                    ByteBuffer buf = ByteBuffer.allocateDirect(1024);

                    String text = "this is my text blahblah";
                    ch.write(encoder.encode(CharBuffer.wrap(text)));

                    ch.position(0);
                    ch.read(buf);

                    buf.flip();
                    String read = decoder.decode(buf).toString();

                    assertThat(read, equalTo(text));

                    buf.clear();

                    ch.position(5);
                    ch.read(buf);

                    buf.flip();
                    read = decoder.decode(buf).toString();

                    assertThat(read, equalTo(text.substring(5)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start().join();
    }
}