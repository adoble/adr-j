package org.doble.adr;

import java.io.IOException;
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

public class CommandNewTest {
	final static private String rootPathName = "/project/adr";
	final static private String docsPath = "/doc/adr";
	private static FileSystem fileSystem;
	private Environment env;
	
	private String[] adrTitles = {"another test architecture decision",
			"yet another test architecture decision",
			"and still the adrs come",
			"to be superseded",
			"some functional name",
			"something to link to",
			"a very important decision"};

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
		String[] args = {"init"};
		ADR.run(args, env);

	}

	@AfterEach
	public void tearDown() throws Exception {
		fileSystem.close();
	}

	@Test
	public void testSimpleCommand() throws Exception {
		String adrTitle = "This is a test architecture decision";

		String[] args = TestUtilities.argify("new " + adrTitle);

		int exitCode = ADR.run(args, env);
		assertEquals(0, exitCode);

		// Check if the ADR file has been created
		assertTrue(Files.exists(fileSystem.getPath("/project/adr/doc/adr/0002-this-is-a-test-architecture-decision.md"))); // ADR id is 2 as the first ADR was setup during init.
	}

	@Test
	public void testWithoutTitle() {
		String[] args = {"new"};
		int exitCode = ADR.run(args,  env);
		
		assertTrue(exitCode == CommandLine.ExitCode.USAGE);  // Usage exit code
	}

	@Test
	public void testManyADRs() throws Exception {

		// Create a set of test paths, paths of files that should be have been created
		ArrayList<Path> expectedFiles = new ArrayList<Path>();
		ArrayList<String> expectedFileNames = new ArrayList<String>();

		for (int id = 0; id < adrTitles.length; id++) {
			String name = TestUtilities.adrFileName(id + 2, adrTitles[id]);
			Path path = fileSystem.getPath(rootPathName, docsPath, name);
			expectedFiles.add(path);
			expectedFileNames.add(path.toString());
		}

		// And now add on the ADR created during initialization
		Path initADR = fileSystem.getPath(rootPathName, docsPath, "0001-record-architecture-decisions.md");
		expectedFiles.add(initADR);
		expectedFileNames.add(initADR.toString());

		for (String adrName : adrTitles) {
			// Convert the name to an array of args - including the command.
			String[] args = ("new" + " " + adrName).split(" ");
			ADR.run(args, env);
		}

		// Check to see if the names exist 
		Path docsDir = fileSystem.getPath(rootPathName, docsPath);

		Stream<String> actualFileNamesStream = Files.list(docsDir).map(Path::toString);
		List<String> actualFileNames = actualFileNamesStream.collect(Collectors.toList());

		assertTrue(actualFileNames.containsAll(expectedFileNames), "File(s) missing");
		assertTrue(expectedFileNames.containsAll(actualFileNames), "Unexpected file(s) found");
	}

	// Tests to check that issue 43 has been corrected
	@Test
	public void testOtherFilesInADRDirectory () throws Exception {
		String adrTitle1 = "Test architecture decision 1";
        String adrTitle2 = "Test architecture decision 2";

		String[] args = TestUtilities.argify("new " + adrTitle1);

		int exitCode = ADR.run(args, env);
		assertEquals(0, exitCode);

		args = TestUtilities.argify("new " + adrTitle2);

		exitCode = ADR.run(args, env);
		assertEquals(0, exitCode);


		// Add a new file that does not follow the ADR naming convention. 
		Files.createFile(fileSystem.getPath(rootPathName, docsPath, "other_doc.md"));  
		
		// Try and create a new ADR. It should work.
		args = TestUtilities.argify("new " + "New decision");

		exitCode = ADR.run(args, env);
		assertEquals(0, exitCode);

		// Add a new directory to the adr directory
		Files.createDirectory(fileSystem.getPath(rootPathName, docsPath, "images"));

		// Try and create a new ADR. It should work.
		args = TestUtilities.argify("new " + "Another decision");

		exitCode = ADR.run(args, env);
		assertEquals(0, exitCode);

	}
	

}
