package ua.tifoha;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

/**
 * Created by Vitaliy Sereda on 01.11.17.
 */
public class AppSerialization {

	public static final int CAPACITY = 100000;

	public static void main(String[] args) throws Exception {
//		Map<Integer, String> map1 = new HashMap<>();
		Map<Integer, String> map;
//		Map<Integer, String> map3 = new ConcurrentHashMap<>();
		final Path path = getConcurrentTempFile();
//		try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(path))) {
		final FSTConfiguration conf = FSTConfiguration.getDefaultConfiguration();
		conf.registerClass(ConcurrentHashMap.class,String.class,Integer.class);
		try (FSTObjectOutput oos = new FSTObjectOutput(Files.newOutputStream(path), conf)) {
		List<Map> maps = new ArrayList<>(CAPACITY);
			for (int i = 0; i < CAPACITY; i++) {
				map = createMap(20, ConcurrentHashMap::new);
//				map = createMap(500, HashMap::new);
				maps.add(map);
				if (i % 1000 == 0) {
					System.out.println(i);
				}
			}
			System.out.println("Serialize");
			int i = 0;
//				oos.writeUnshared(maps);
//				oos.writeObject(maps);
//
			for (Map m : maps) {
//				oos.writeUnshared(m);
				oos.writeObject(m);
				i++;
				if (i % 1000 == 0) {
					System.out.println("S:" + i);
//					oos.reset();
//					oos.flush();
				}
			}

//			map = createMap(200, ConcurrentHashMap::new);
//			oos.writeObject(map);
		}
		System.out.println("Size: " + Files.size(path));
//		try (FSTObjectInput ois = new FSTObjectInput(Files.newInputStream(path))) {
//			List<Map> maps = new ArrayList<>(CAPACITY);
//			for (int i = 0; i < CAPACITY; i++) {
//				map = (Map<Integer, String>) ois.readObject();
////				map = createMap(500, HashMap::new);
//				maps.add(map);
//				if (i % 1000 == 0) {
//					System.out.println(i);
////					ois.reset();
//				}
//			}
//			System.out.println();
//		}

//		System.gc();
		Files.delete(path);
		System.out.println();
		System.out.println(Files.exists(path));
//		System.gc();
//		TimeUnit.SECONDS.sleep(5);
	}

	private static Path getConcurrentTempFile() throws IOException {
		return Files.createTempFile("concurrent", "tmp");
	}

	private static File getPlainTempFile() throws IOException {
		return File.createTempFile("concurrent", "tmp");
	}

	private static Map<Integer, String> createMap(int size, Supplier<Map> constructor) {
		Map<Integer, String> map = constructor.get();
		for (int i = 0; i < size; i++) {
			final String value = String.valueOf(i);
			map.put(i, value);
		}
		return map;
	}

//	private static class MyObjectOutputStream extends ObjectOutputStream {
//		public MyObjectOutputStream(OutputStream outputStream) throws IOException {
//			super();
//			super.obje
//		}
//	}
}
