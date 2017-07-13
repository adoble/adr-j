package org.doble.adr;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class TestUtilities {
   
	
	public static void ls(Path directory) {
		
		System.out.println("Files in " + directory.toString() + " -------------->");
		
		try (Stream<Path> stream = Files.list(directory)) {
			stream.forEach(System.out::println);
		}

		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("<--------------------");
		
		
	}
	
	
	
}
