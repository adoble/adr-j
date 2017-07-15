package org.doble.adr;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

public class TestUtilities {
	

   
	
	/** Print out the files in the specified directory */
	public static void ls(Path directory) {
		
		System.out.println("Files in " + directory.toString() + " -------------->");
		
		try (Stream<Path> stream = Files.list(directory)) {
			stream.forEach(System.out::println);
		}

		catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("<--------------------");
		
		
	}
	
	/** Generate the file name of an ADR */
	public static String adrFileName (int id, String title) {
	
	  return String.format("%04d", id) + "-" + title.replace(' ', '-').toLowerCase() + ".md";
	}
	
	
	/** Find the number of time a string appears in an ADR */
	public static long findString(String lookFor, Path file) throws Exception {
	    long count;
	    
		count = Files.lines(file).filter(s -> s.contains(lookFor)).count();
		return count;
	    
	}
	
	
}
