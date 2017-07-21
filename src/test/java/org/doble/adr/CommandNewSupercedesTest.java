package org.doble.adr;

import static org.junit.Assert.*;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.*;
import org.junit.runners.MethodSorters;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

/**
 * 
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CommandNewSupercedesTest 
{
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

	//private Environment env;
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

		adr = new ADR(new Environment.Builder(fileSystem)
				.out(System.out)
				.err(System.err)
				.in(System.in)
				.userDir(rootPathName)
				.editorRunner(new TestEditorRunner())
				.build()
				);
		
		// Set up the directory structure
		String[] args = {"init"};
		try {
			adr.run(args);
		} catch (ADRException e) {
			fail("ADR Exception raised");
		}


		// Now create a set of files that we can use for the tests
		//for (String adrTitle: adrTitles) {
		for (String adrTitle: adrTitles) {
				// Convert the name to an array of args - including the command.
			args = ("new" + " " + adrTitle).split(" ");
			try {
				adr.run(args);
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
		
	    try {
			checkSupersedes(supercededIds);
		} catch (ADRException e) {
			fail(e.getMessage());
		}
	
	}
	
	@Test
	public void test2MultipleSupersedes() {
		int[] supercededIds = {5, 6, 8};
		
	    try {
	    	checkSupersedes(supercededIds);
	    }
	    catch (ADRException e) {
	    	fail(e.getMessage());
	    }
	}
	
	
	
	@Test
	public void test3SupercedesInvalidADR() {
		
		// badly formed id 
		String[] adrIds = {"foo"};
		try {
			checkSupersedes(adrIds);
			fail("Exception not raised when ADR is incorrectly formed");
		} catch (ADRException e) {
			assertTrue(true);
		}
		
		// Non existing adr
		String[] neAdrIds = {"100"};
		try {
			checkSupersedes(neAdrIds);
			fail("Exception not raised when ADR does not exist");
		} catch (ADRException e) {
			assertTrue(true);
		}
		
	}
	

	
	public void checkSupersedes(int[] supercededIds) throws ADRException {
		String[] strings = new String[supercededIds.length];
		
		for (int i = 0; i < supercededIds.length; i++ ) {
			strings[i] = Integer.toString(supercededIds[i]);
		}
		
		checkSupersedes(strings); 
	}
	
	
	

	/**
	 * @param supercededIds
	 */
	public void checkSupersedes(String[] supercededIds) throws ADRException {
//		int[] supercededIds = Arrays.stream(supercededIdStrings).mapToInt(Integer::parseInt).toArray();
//		
//		// Checks to ensure the integrity of the test data
//		OptionalInt highest = Arrays.stream(supercededIds).max();
//	    assertTrue(highest.getAsInt() < adrTitles.length + 2);
//	    OptionalInt lowest = Arrays.stream(supercededIds).min();
//	    assertTrue(lowest.getAsInt() > 1);  //i.e not the initial ADR or negative
	    
		
		// Now create a new ADR that supersedes a number of ADRs
		String newADRTitle = "This superceeds number";
		for (String index: supercededIds) {
			newADRTitle += " " + index;
		}
		
		// Create a new ADR that supersedes others 
		ArrayList<String> argList = new ArrayList<String>(); 
		argList.add("new");
		for (String id: supercededIds) {
			argList.add("-s");
			argList.add(id);
		}
		argList.addAll(new ArrayList<String>(Arrays.asList((newADRTitle).split(" "))));

		String[] args = {}; 
		args = argList.toArray(args);
		//try {
			adr.run(args);
//	} catch (ADRException e) {
//		fail(e.getMessage());
//	}
		
		// Check that the the new record mentions that it supersedes ADR the ids
		int newADRID = adrTitles.length + 2;
		String newADRFileName = TestUtilities.adrFileName(newADRID, newADRTitle);
		Path newADRFile = fileSystem.getPath(rootPathName, docsPath, newADRFileName);
		
		for (String supersededADRID: supercededIds ) {
			long count = 0;
			String title = adrTitles[(new Integer(supersededADRID)).intValue() - 2];
			String supersededADRFileName = TestUtilities.adrFileName(supersededADRID, title);
		    String link = "Supersedes the [architecture decision record "  + supersededADRID + "](" + supersededADRFileName + ")";
			try {
				count = TestUtilities.findString(link, newADRFile);
			} catch (Exception e) {
				fail(e.getMessage());
			}

			assertTrue("The new ADR does not reference the superseded ADR [" + supersededADRID + "] in the text.", count == 1);
		}
		
		// Check that the superseded ADRs reference the ADR that supersedes them 
		for (String supersededADRID: supercededIds ) {
			long count = 0;
			String title = adrTitles[(new Integer(supersededADRID)).intValue() - 2];
			String supersededADRFileName = TestUtilities.adrFileName(supersededADRID, title);
			Path supersededADRFile = fileSystem.getPath("/project/adr/doc/adr/", supersededADRFileName);
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
