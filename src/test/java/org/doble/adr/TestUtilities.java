package org.doble.adr;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public class TestUtilities {
	private enum States {initial, character, space, quote}

	/**
	 * Print out the files in the specified directory
	 */
	public static void ls(Path directory) {

		System.out.println("Files in " + directory.toString() + " -------------->");

		try (Stream<Path> stream = Files.list(directory)) {
			stream.forEach(System.out::println);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("<--------------------");
	}

	/**
	 * Generate an argument array from a string
	 */
	public static String[] argify(String s) {
		ArrayList<String> args = new ArrayList<String>(Arrays.asList(s.split(" ")));
		ArrayList<String> finalArgs = new ArrayList<String>();

		// Check if any args have quotes and pack them together
		String packedArg = "";
		boolean packing = false;
		for (String arg : args) {
			if (arg.startsWith("\"")) {
				packedArg += arg.substring(1);
				packing = true;
			} else if (arg.endsWith("\"")) {
				packedArg += " " + arg.substring(0, arg.length() - 1);
				finalArgs.add(packedArg);
				packing = false;
			} else {
				if (!packing) finalArgs.add(arg);
				else packedArg += " " + arg;
			}
		}

		String[] returnedArgs = {};
		return finalArgs.toArray(returnedArgs);
	}

	/**
	 * Generate the file name of an ADR
	 */
	public static String adrFileName(int id, String title) {

		return String.format("%04d", id) + "-" + title.replace(' ', '-').toLowerCase() + ".md";
	}

	/**
	 * Generate the file name of an ADR
	 */
	public static String adrFileName(String idstr, String title) {
		int id = new Integer(idstr).intValue();

		return adrFileName(id, title);
	}

	/**
	 * Does the file contains only one instance of the specified string?
	 */
	public static boolean contains(String s, Path file) throws Exception {
		return (findString(s, file) == 1);
	}

	/**
	 * Find the number of time a string appears in an ADR
	 */
	public static long findString(String lookFor, Path file) throws Exception {
		long count;

		//Test point
		Files.lines(file).forEach(System.out::println);

		count = Files.lines(file).filter(s -> s.contains(lookFor)).count();
		return count;
	}
}
