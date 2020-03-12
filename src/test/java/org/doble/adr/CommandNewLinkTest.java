package org.doble.adr;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import picocli.CommandLine;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

//import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CommandNewLinkTest {
	final static private String rootPathName = "/project/adr";
	final static private String docsPath = "/doc/adr";

	private static FileSystem fileSystem;

	private Environment env;

	@BeforeEach
	public void setUp() throws Exception {
		Path rootPath = null;

		// Set up the mock file system
		fileSystem = Jimfs.newFileSystem(Configuration.unix());

		rootPath = fileSystem.getPath("/project");

		Files.createDirectory(rootPath);

		// Set up the environment
		env = new Environment.Builder(fileSystem)
				.out(System.out)
				.err(System.err)
				.in(System.in)
				.userDir(rootPathName)
				.editorCommand("dummyEditor")
				.editorRunner(new TestEditorRunner())
				.build();

		// Initialize up the directory structure
		String[] args = {"init"};
		int exitCode = ADR.run(args, env);
		
		assertTrue(exitCode == 0); // Successful initialization
		
	}

	@AfterEach
	public void tearDown() throws Exception {
		fileSystem.close();
	}

	/** 
	 * Create some valid links to ADRs and check if they are added. 
	 * @throws Exception
	 */
	@Test
	public void testLinks() throws Exception {
		// Create some ADRs
		assertEquals(ADR.run(TestUtilities.argify("new An ADR"), env), 0);
		assertEquals(ADR.run(TestUtilities.argify("new Yet another adr"), env), 0);
		assertEquals(ADR.run(TestUtilities.argify("new This ADR is going to be linked to"), env), 0);  // ADR id 4
		assertEquals(ADR.run(TestUtilities.argify("new And even more decisions"), env), 0);
		assertEquals(ADR.run(TestUtilities.argify("new Decisions decisions decisions"), env), 0);

		// Create new ADR that links to another
		assertEquals(ADR.run(TestUtilities.argify("new -l \"4:Links to\" Links to number 4"), env), 0);

		// Now check if the link messages has been added to the new ADR 7
		Path newADRFile = fileSystem.getPath(rootPathName, docsPath, "0007-links-to-number-4.md");
        
		assertTrue(TestUtilities.contains("Links to [ADR 4](0004-this-adr-is-going-to-be-linked-to.md)", newADRFile));

	}

	/**
	 * Create a invalid link specification and see if it is detected
	 */
	@Test
	public void testMissingLInkSpec() {
		// Create new ADR that links to another, but with a malformed Link specification
		int exitCode = ADR.run(TestUtilities.argify("new -l Links to number 4"), env);
		assertEquals(exitCode, CommandLine.ExitCode.USAGE);
	}
	
	/**
	 * Create a link to an ADR that does not exists and see if it is detected
	 * 
	 */
	@Test 
	public void testNonexistingLink() {
		
		assertEquals(ADR.run(TestUtilities.argify("new -l 42:\"Links to nowhere\" Links should be valid"), env), 
				    CommandLine.ExitCode.SOFTWARE);
		
		
	}
	
	
	
	
	
}
