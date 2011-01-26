package claro.catalog.impl.importing;

import java.io.File;
import java.net.MalformedURLException;

public class Tests {

	/**
	 * @param args
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws MalformedURLException {

		System.out.println(new File("file:/parent/").getParent());
	}

}
