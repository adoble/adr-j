package org.doble.adr;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import picocli.CommandLine;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Disabled;

//import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CommandNewSupersedesTest {
	final static private String rootPathName = "/project/adr";
	final static private String docsPath = "/doc/adr";

	final private String[] adrTitles = {"Another test architecture decision",
			"Yet another test architecture decision",
			"and still the adrs come",
			"to be superseded",
			"some functional name",
			"something to link to",
			"a very important decision"};

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

		// Initalize up the directory structure
		String[] args = {"init"};
		ADR.run(args, env);

		// Now create a set of files that we can use for the tests
		//for (String adrTitle: adrTitles) {
		for (String adrTitle : adrTitles) {
			// Convert the name to an array of args - including the command.
			args = ("new" + " " + adrTitle).split(" ");
			ADR.run(args, env);
		}
	}

	@AfterEach
	public void tearDown() throws Exception {
		fileSystem.close();
	}

	@Test
	@Order(1)
	public void test1Superseded() throws Exception {
		int[] supersededIds = {5};
		checkSupersedes(supersededIds);
	}

	@Test
	@Order(2)
	public void test2MultipleSupersedes() throws Exception {
		int[] supersededIds = {5, 6, 8};
		checkSupersedes(supersededIds);
	}

	@Test
	@Order(3)
	public void test3SupersedesInvalidADR() throws Exception {
       int exitCode;
       
		// badly formed id 
		String[] adrIds = {"foo"};
		
		exitCode = checkSupersedes(adrIds);
		assertEquals(CommandLine.ExitCode.USAGE, exitCode);  

		// Non existing adr
		String[] nonExistingAdrIds = {"100"};
		exitCode = checkSupersedes(nonExistingAdrIds);
		
		assertEquals(CommandLine.ExitCode.SOFTWARE, exitCode);  
		
	}

	public int checkSupersedes(int[] supersededIds) throws Exception {
		String[] strings = new String[supersededIds.length];

		for (int i = 0; i < supersededIds.length; i++) {
			strings[i] = Integer.toString(supersededIds[i]);
		}

		int exitCode = checkSupersedes(strings);
		return exitCode;
	}

	public int checkSupersedes(String[] supersededIds) throws Exception {
		int exitCode = 0;

		// Now create a new ADR that supersedes a number of ADRs
		String newADRTitle = "This supersedes number";
		for (String index : supersededIds) {
			newADRTitle += " " + index;
		}

		// Create a new ADR that supersedes others 
		ArrayList<String> argList = new ArrayList<String>();
		argList.add("new");
		for (String id : supersededIds) {
			argList.add("-s");
			argList.add(id);
		}
		argList.addAll(new ArrayList<String>(Arrays.asList((newADRTitle).split(" "))));

		String[] args = {};
		args = argList.toArray(args);
		exitCode = ADR.run(args, env);
		
		if (exitCode != 0) {  // Failure
		  return exitCode;
		}  

		// Check that the the new record mentions that it supersedes ADR the ids
		int newADRID = adrTitles.length + 2;
		String newADRFileName = TestUtilities.adrFileName(newADRID, newADRTitle);
		Path newADRFile = fileSystem.getPath(rootPathName, docsPath, newADRFileName);

		for (String supersededADRID : supersededIds) {
			long count = 0;
			//String title = adrTitles[(new Integer(supersededADRID)).intValue() - 2];
			String title = adrTitles[Integer.parseInt(supersededADRID) - 2]; 
			String supersededADRFileName = TestUtilities.adrFileName(supersededADRID, title);
			//* Supersedes [ADR{{{superseded.id}}}]({{{superseded.file}}})
			//String link = "Supersedes ADR " + supersededADRID + " - " + supersededADRFileName ;
			//Default template:
			// "Supersedes [ADR {{{superseded.id}}}]({{{superseded.file}}})"
			String link = "Supersedes [ADR " + supersededADRID + "](" + supersededADRFileName + ")";
			count = TestUtilities.findString(link, newADRFile);
			assertTrue(count == 1, "The new ADR does not reference the supersceded ADR [" + supersededADRID + "] in the text.");
		}

		// Check that the superseded ADRs reference the ADR that supersedes them 
		// REMOVE: With user defined templates cannot reliably insert this message
		// TODO: Check that this functionality is in help files
		// TODO: check that this is mentioned in the documentation. 
//		for (String supersededADRID : supersededIds) {
//			long count = 0;
//			String title = adrTitles[(new Integer(supersededADRID)).intValue() - 2];
//			String supersededADRFileName = TestUtilities.adrFileName(supersededADRID, title);
//			Path supersededADRFile = fileSystem.getPath("/project/adr/doc/adr/", supersededADRFileName);
//			String link = "Superseded by the [architecture decision record " + newADRID + "](" + newADRFileName + ")";
//			count = TestUtilities.findString(link, supersededADRFile);
//			assertTrue(count == 1, "The superseded ADR does not reference the  (new) ADR [" + supersededADRID + "] that supersedes it in the text.");
//		}
		
		return exitCode;
	}
}
