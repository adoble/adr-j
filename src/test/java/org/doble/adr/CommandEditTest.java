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

public class CommandEditTest {
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

		// Set up the directory structure (including the initial file)
		String[] args = {"init"};
		ADR.run(args, env);

		// Now set up a set of ADRs
    for (String adrTitle: adrTitles) {
			args = TestUtilities.argify("new " + adrTitle);
			ADR.run(args, env);
		}
	}

	@AfterEach
	public void tearDown() throws Exception {
		fileSystem.close();
	}

	@Test
	public void testEditADR() throws Exception {
		int adrId = 3;

		assertTrue(adrId <= adrTitles.length + 1); // Remembering the initial ADR
		String[] args = TestUtilities.argify("edit " + adrId);

		int exitCode = ADR.run(args, env);
		assertEquals(CommandLine.ExitCode.OK, exitCode);

	}

	@Test
	public void testEditNonExistingADR() {
		int adrId = adrTitles.length + 2;

		String[] args = TestUtilities.argify("edit " + adrId);

		int exitCode = ADR.run(args, env);
		assertEquals(CommandLine.ExitCode.USAGE, exitCode);
	}
	
	@Test
	public void testEditMalformedId() {
		String malformedID = "4!";
		
		String[] args = TestUtilities.argify("edit " + malformedID);

		int exitCode = ADR.run(args, env);
		assertEquals(CommandLine.ExitCode.USAGE, exitCode);
	}

	


}
