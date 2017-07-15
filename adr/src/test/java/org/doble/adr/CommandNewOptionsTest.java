package org.doble.adr;

//import junit.framework.Test;
//import junit.framework.TestCase;
//import junit.framework.TestSuite;

import static org.junit.Assert.*;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.OptionalInt;

import org.junit.*;
import org.junit.runners.MethodSorters;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

/**
 * 
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CommandNewOptionsTest 
{
	final static private String rootPathName = "/project/adr";
	final static private String docsPath = "/docs/adr";

	final private String[] adrTitles = {"Another test architecture decision", 
			"Yet another test architecture decision",
			"and still the adrs come", 
			"to be superseded", 
			"some functional name",
			"something to link to", 
	        "a very important decision"};
	
	

	private static FileSystem fileSystem;

	private Environment env;
	private ADR adr;

	@Before
	public void setUp() throws Exception {
		Path rootPath = null; 

		// Set up the mock file system
		try {
			fileSystem = Jimfs.newFileSystem(Configuration.unix());

			rootPath = fileSystem.getPath("/project");

			Files.createDirectory(rootPath);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Set up the environment
		env = new Environment.Builder(fileSystem)
				.out(System.out)
				.err(System.err)
				.in(System.in)
				.userDir(rootPathName)
				.editorRunner(new TestEditorRunner(null))
				.build();

		// Set up the directory structure
		adr = new ADR();

		String[] args = {"init"};
		try {
			adr.run(args, env);
		} catch (ADRException e) {
			fail("ADR Exception raised");
		}


		// Now create a set of files that we can use for the tests
		//for (String adrTitle: adrTitles) {
		for (String adrTitle: adrTitles) {
				// Convert the name to an array of args - including the command.
			args = ("new" + " " + adrTitle).split(" ");
			try {
				adr.run(args, env);
			} catch (ADRException e) {
				fail(e.getMessage());
			}
		}


	}

	@After
	public void tearDown() throws Exception {
		fileSystem.close();
	}


	@Test
	public void test1Superseded() {
        int[] supercededIds = {5};
		
	    checkSupersedes(supercededIds);
		
//		Path supersededADRFile = fileSystem.getPath("/project/adr/docs/adr/0005-to-be-superseded.md");
//
//		// Now create a new ADR that supersedes ADR 5.
//		int supersededId = 5;
//		String newADRTitle = "This superceeds number 5";
//		ArrayList<String> argList = new ArrayList<String>(); 
//		argList.add("new");
//		argList.add("-s");
//		argList.add(Integer.toString(supersededId));
//		argList.addAll(new ArrayList<String>(Arrays.asList((newADRTitle).split(" "))));
//
//		String[] args = {}; 
//		args = argList.toArray(args);
//		try {
//			adr.run(args, env);
//		} catch (ADRException e) {
//			fail(e.getMessage());
//		}
//
//		// Check that the the new record mentions that it supersedes ADR 5
//		int supersededADRID = 5;
//		int newADRID = adrTitles.length + 2;
//		String newADRFileName = TestUtilities.adrFileName(newADRID, newADRTitle);
//
//		Path newADRFile = fileSystem.getPath(rootPathName, docsPath, newADRFileName);
//		long count = 0;
//		String link = "Supersedes the [architecture decision record "  + supersededADRID + "](" + supersededADRFile.getFileName() + ")";
//		try {
//			count = TestUtilities.findString(link, newADRFile);
//		} catch (Exception e) {
//			fail(e.getMessage());
//		}
//
//		assertEquals("The new ADR does not reference the superseded ADR in the text.", count, 1);
//
//
//
//		count = 0;
//		try {				
//			link = "Superseded by the [architecture decision record "  + newADRID + "](" + newADRFileName + ")";
//			count = TestUtilities.findString(link, supersededADRFile );
//			assertEquals("The superseded ADR does not reference the ADR that superseded it in the text.", 1, count);
//
//		}
//		catch (Exception e) {
//			fail(e.getMessage());
//		}
		
		
		

	}
	
	@Test
	public void test2MultipleSupersedes() {
		int[] supercededIds = {5, 6, 8};
		
	    checkSupersedes(supercededIds);
		
		
	}

	/**
	 * @param supercededIds
	 */
	public void checkSupersedes(int[] supercededIds) {
		// Checks to ensure the integrity of the test data
		OptionalInt highest = Arrays.stream(supercededIds).max();
	    assertTrue(highest.getAsInt() < adrTitles.length + 2);
	    OptionalInt lowest = Arrays.stream(supercededIds).min();
	    assertTrue(lowest.getAsInt() > 1);  //i.e not the initial ADR or negative
	    
		
		// Now create a new ADR that supersedes a number if ADrs
		String newADRTitle = "This superceeds number";
		for (int index: supercededIds) {
			newADRTitle += " " + index;
		}
		
		// Create a new ADr that supersedes others 
		ArrayList<String> argList = new ArrayList<String>(); 
		argList.add("new");
		for (int id: supercededIds) {
			argList.add("-s");
			argList.add(Integer.toString(id));
		}
		argList.addAll(new ArrayList<String>(Arrays.asList((newADRTitle).split(" "))));

		String[] args = {}; 
		args = argList.toArray(args);
		try {
			adr.run(args, env);
		} catch (ADRException e) {
			fail(e.getMessage());
		}
		
		// Check that the the new record mentions that it supersedes ADR the ids
		int newADRID = adrTitles.length + 2;
		String newADRFileName = TestUtilities.adrFileName(newADRID, newADRTitle);
		Path newADRFile = fileSystem.getPath(rootPathName, docsPath, newADRFileName);
		
		for (int supersededADRID: supercededIds ) {
			long count = 0;
			String supersededADRFileName = TestUtilities.adrFileName(supersededADRID, adrTitles[supersededADRID - 2]);
		    String link = "Supersedes the [architecture decision record "  + supersededADRID + "](" + supersededADRFileName + ")";
			try {
				count = TestUtilities.findString(link, newADRFile);
			} catch (Exception e) {
				fail(e.getMessage());
			}

			assertTrue("The new ADR does not reference the superseded ADR [" + supersededADRID + "] in the text.", count == 1);
		}
		
		// Check that the superseded ADRs reference the ADR that supersedes them 
		for (int supersededADRID: supercededIds ) {
			long count = 0;
			String supersededADRFileName = TestUtilities.adrFileName(supersededADRID, adrTitles[supersededADRID - 2]);
			Path supersededADRFile = fileSystem.getPath("/project/adr/docs/adr/", supersededADRFileName);
			String link = "Superseded by the [architecture decision record "  + newADRID + "](" + newADRFileName + ")";
			try {
				count = TestUtilities.findString(link, supersededADRFile);
			} catch (Exception e) {
				fail(e.getMessage());
			}
			assertTrue("The superseded ADR does not reference the  (new) ADR [" + supersededADRID + "] that supersedes it in the text.", count == 1);
		}
	}
}
