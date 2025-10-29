package org.doble.adr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import picocli.CommandLine;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This is a test for issue 62. 
 * It tests the following scenario:
 * 1) User has configured ADRs to be stored in a different directory to the default /doc/adr as in:
 *    ```
 * 	adr init docs/architecture/decisions
 * 	```	
 * 2) User cds into docs: 
 * ```
 * 	cd docs
 * ```
 * 3) User runs 
 * ```
 * adr new what ever decision
 * ```
 * 
 * @author adoble
 *
 */

public class CommandNewWithConfiguredDocTest {
	final static private String rootPathName = "/project/adr";
	final static private String docsPath = "docs/architecture/decisions";
	private static FileSystem fileSystem;
	private Environment env;

	
	@BeforeEach
	public void setUp() throws Exception {
		Path rootPath = null;

		// Set up the mock file system
		fileSystem = Jimfs.newFileSystem(Configuration.unix());

		rootPath = fileSystem.getPath("/project");

		Files.createDirectory(rootPath);

		env = new Environment.Builder(fileSystem)
				.out(System.out)
				.err(System.err)
				.in(System.in)
				.userDir(rootPathName)
				.editorCommand("dummyEditor")
				.editorRunner(new TestEditorRunner())
				.build();

		// Set up the directory structure
		String[] args = { "init", docsPath };
		ADR.run(args, env);

	}

	@AfterEach
	public void tearDown() throws Exception {
		fileSystem.close();
	}

	@Test
	public void testNewInDocsDir() throws Exception {
		String adrTitle = "What ever decision";

		// CD to docs directory
		env = new Environment.Builder(fileSystem)
				.out(System.out)
				.err(System.err)
				.in(System.in)
				.userDir(rootPathName + "/docs")
				.editorCommand("dummyEditor")
				.editorRunner(new TestEditorRunner())
				.build();

		String[] args = TestUtilities.argify("new " + adrTitle);

		int exitCode = ADR.run(args, env);
		assertEquals(0, exitCode);

		// Check if the ADR file has been created.
		// ADR id is 2 as the first ADR was setup during init.
		assertTrue(
				Files.exists(fileSystem.getPath("/project/adr/docs/architecture/decisions/0002-what-ever-decision.md")),	
				"ADR file was not created in configured docs directory"
				); 
				
																														
	}

	

}
