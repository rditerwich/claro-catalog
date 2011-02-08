package claro.catalog.command.items;

import java.util.Arrays;
import java.util.Comparator;

import org.junit.Test;

public class Experiment {
	private enum HelloWorld { World,  Hello };

	@Test
	public void test() {
		System.out.println("Values:");
		for (HelloWorld h : HelloWorld.values()) {
			System.out.println(" " + h);
		}
		
		HelloWorld[] w = HelloWorld.values();
		
		Arrays.sort(w, new Comparator<HelloWorld>() {
			public int compare(HelloWorld o1, HelloWorld o2) {
				return o1.name().compareTo(o2.name());
			}
		});
		
		System.out.println("Values:");
		for (HelloWorld h : HelloWorld.values()) {
			System.out.println(" " + h + " (o: " + h.ordinal() + ")");
		}

		System.out.println("w:");
		for (HelloWorld h : w) {
			System.out.println(" " + h + " (o: " + h.ordinal() + ")");
		}
		
		for (int i = 0; i < 10; i++) {
			System.out.println(HelloWorld.values());
		}
	}
}
