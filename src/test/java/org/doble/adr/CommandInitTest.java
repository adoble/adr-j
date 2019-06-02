package org.doble.adr;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommandInitTest {
	private static FileSystem fileSystem;
	final static private String rootPath = "/project/adr";

	private Environment env;

	@BeforeEach
	public void setUp() throws Exception {

		// Set up the mock file system
		fileSystem = Jimfs.newFileSystem(Configuration.unix());

		Path projectPath = fileSystem.getPath("/project");
		Files.createDirectory(projectPath);

		// Set up the environment
		env = new Environment.Builder(fileSystem)
				.out(System.out)
				.err(System.err)
				.in(System.in)
				.userDir(rootPath)
				.editorCommand("dummyEditor")
				.build();
	}

	@AfterEach
	public void tearDown() throws Exception {
		fileSystem.close();
	}

	@Test
	public void testInit() throws Exception {
		//ADR adr = new ADR(env);

		String[] args = {"init"};

		ADR.run(args, env);

		// Check to see if the .adr directory has been created.
		String pathName = rootPath + "/.adr";
		Path p = fileSystem.getPath(pathName);
		boolean exists = Files.exists(p);
		assertTrue(exists);

		//Now see if the  standard docs directory has been created
		pathName = rootPath + "/doc/adr";
		p = fileSystem.getPath(pathName);
		exists = Files.exists(p);
		assertTrue(exists);

		// Check if the ADR has been created
		pathName = rootPath + "/doc/adr/0001-record-architecture-decisions.md";
		p = fileSystem.getPath(pathName);
		exists = Files.exists(p);
		assertTrue(exists);

		// Do a sample check on the content
		pathName = rootPath + "/doc/adr/0001-record-architecture-decisions.md";
		p = fileSystem.getPath(pathName);
		List<String> contents = Files.readAllLines(p);

		// Sample the contents
		int matches = 0;
		for (String line : contents) {
			if (line.contains("Record architecture decisions")) matches++;
			if (line.contains("## Decision")) matches++;
			if (line.contains("Nygard")) matches++;
		}

		assertTrue(matches == 4);
	}

	@Test
	public void testInitCustomDirectory() throws Exception {
		String customDir = "myStuff/myDocs/myADRs";

		//ADR adr = new ADR(env);

		String[] args = {"init", customDir};

		ADR.run(args, env);

		// Check to see if the custom directory has been created. 
		String pathName = rootPath + "/" + customDir;
		Path p = fileSystem.getPath(pathName);
		boolean exists = Files.exists(p);
		assertTrue(exists);
	}

	/**
	 * Test to see if a re initialization of the directory causes error code to be given
	 */
	@Test
	public void testReInit() throws Exception {
		int errorCode;
		//ADR adr = new ADR(env);

		// Initialize the ADR directories
		String[] args = {"init"};

		 errorCode = ADR.run(args, env);
		
		assertTrue(errorCode == 0);

		// Re-initialize to see if an error code is given  is raised
	
		errorCode = 	ADR.run(args, env);
		
		assertTrue(errorCode == ADR.ERRORCODE);
			
	}
	
	//TODO test for other error codes. 

	/**
	 * Test to see if the init command still goes through even
	 * if the EDITOR or VISUAL environment variable has not been set.
	 */
	@Test
	public void testEditorNotSet() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream testErr = new PrintStream(baos);
		// Set up the environment with a null editor command and catch the output
		Environment envWithoutEditor = new Environment.Builder(fileSystem)
				.out(env.out)
				.err(testErr)
				.in(env.in)
				.userDir(env.dir)
				.editorCommand(null)
				.build();

		//ADR adr = new ADR(envWithoutEditor);

		String[] args = {"init"};

		ADR.run(args, envWithoutEditor);
		String commandOutput = new String(baos.toByteArray());
		assertTrue(commandOutput.contains("WARNING"), "No warning given from init command that edit has not been set.");

		// Check to see if the .adr directory has been created even though an error is there .
		String pathName = rootPath + "/.adr";
		Path p = fileSystem.getPath(pathName);
		boolean exists = Files.exists(p);
		assertTrue(exists);
	}
}
