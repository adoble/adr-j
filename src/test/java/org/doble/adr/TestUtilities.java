package org.doble.adr;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class TestUtilities {

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
				if (!packing)
					finalArgs.add(arg);
				else
					packedArg += " " + arg;
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
		int id = Integer.parseInt(idstr);

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

		count = Files.lines(file).filter(s -> s.contains(lookFor)).count();
		return count;
	}

	/**
	 * Print a two multi-line string each line after the other for comparison
	 * 
	 * @param expectedContents
	 * @param actualContents
	 */
	public static void stringDiff(String expectedContents, String actualContents) {
		List<String> ex = Stream
				.of(expectedContents.split("\n"))
				.collect(Collectors.toList());
		List<String> ac = Stream
				.of(actualContents.split("\n"))
				.collect(Collectors.toList());
		for (int i = 0; i < ex.size(); i++) {
			System.out.println(">" + ex.get(i));
			System.out.println("<" + ac.get(i));

		}
	}

	/**
	 * Print out the contents of the specified file.
	 * 
	 * @param path The file to print out
	 */
	public static void printFile(Path path) {

		try (Stream<String> lines = Files.lines(path)) {

			String contents = lines.collect(Collectors.joining("\n"));
			System.out.print(contents);

		} catch (IOException e) {
			System.err.println("Unable to read print file " + path.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Create a (mock) template file based on the specified content
	 * 
	 * @param fileSystem              The file system hosting the files
	 * @param initTemplateFileName    The full path name of the mock template file
	 * @param initTemplateFileContent Contents of the mock template
	 */
	public static void createTemplateFile(FileSystem fileSystem, String templateFileName, String templateContent) {

		Path templateFile = fileSystem.getPath(templateFileName);
		Path templateDirectory = templateFile.getParent();
		try {
			Files.createDirectories(templateDirectory);
			Files.write(templateFile, templateContent.getBytes());
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Split the input string into lines, remove trailing whitespaces from each
	 * line and reduce multiple empty lines to just one empty line.
	 */
	public static String trimContent(String input) {

		StringBuilder result = new StringBuilder();
		boolean previousLineWasEmpty = false;

		for (String line : input.lines().map(String::stripTrailing).collect(Collectors.toList())) {
			if (line.isEmpty()) {
				if (!previousLineWasEmpty) {
					result.append(System.lineSeparator()); // Add one empty line
				}
				previousLineWasEmpty = true;
			} else {
				result.append(line).append(System.lineSeparator());
				previousLineWasEmpty = false;
			}
		}

		// Remove the trailing newline if the result ends with an empty line
		if (result.length() > 0 && result.charAt(result.length() - 1) == '\n') {
			result.deleteCharAt(result.length() - 1);
		}

		return result.toString();
	}

	/**
	 * 
	 * Removes all files (and NOT directories) in a specified directory.
	 * The function throws an exception if the provided path is not a valid
	 * directory.
	 */

	public static void removeAllFilesInDirectory(Path directory) throws IOException {

		// Ensure the directory exists
		if (!Files.isDirectory(directory)) {
			throw new IllegalArgumentException("The provided path is not a directory.");
		}

		// Use Files.walkFileTree to iterate over the files
		Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				// Delete each file
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				// Handle file visit failure (e.g., permission issues)
				System.err.println("Failed to access file: " + file + " due to " + exc.getMessage());
				return FileVisitResult.CONTINUE;
			}
		});
	}

}
