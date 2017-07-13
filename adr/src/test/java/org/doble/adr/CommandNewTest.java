package org.doble.adr;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import org.doble.adr.ADR;
import org.doble.adr.ADRException;
import org.doble.adr.Environment;
import org.junit.*;


import org.hamcrest.CoreMatchers.*;
import org.junit.Assert.*;

import com.google.common.jimfs.*;




public class CommandNewTest {
	private static FileSystem fileSystem;
    final static private String rootPathName = "/project/adr";
    final static private String docsPath = "/docs/adr";
    
    private String[] adrNames = {"another test architecture decision", 
    		                     "yet another test architecture decision",
    		                     "and still the adrs come", 
    		                     "to be superseded", 
    		                     "some functional name",
    		                     "something to link to", 
    		                     "a very important decision"};
    
    private Environment env;
    private ADR adr;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		Path rootPath = null; 
		
		// Set up the mock file system
		try {
			fileSystem = Jimfs.newFileSystem(Configuration.unix());

			rootPath = fileSystem.getPath("/project");

			Files.createDirectory(rootPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			fail("ADR Exception raised");
		}

		System.out.print("CommandNewTest setup ");
		TestUtilities.ls(fileSystem.getPath("/project/adr/docs/adr"));
		
	}

	@After
	public void tearDown() throws Exception {
		fileSystem.close();
	}

	@Test
	public void testSimpleCommand() {
		String adrName = "This is a test achitecture decision";
	
		
		// Convert the name to an array of args - including the command.
		String[] args = ("new" + " " + adrName).split(" ");
				
		try {
			adr.run(args, env);
		} catch (ADRException e) {
			// TODO Auto-generated catch block
			fail("ADR Exception raised");
		}
		
		
		// Check if the ADR file has been created
		// First construct the file name
		String fileName = ("0002 " + adrName +".md").replace(' ',  '-').toLowerCase();  // ADR id is 2 as the first ADR was setup during init.
		Path adrFile = fileSystem.getPath(rootPathName, docsPath, fileName);
		
		//TestUtilities.ls(adrFile.getParent());
		
		
		boolean exists = Files.exists(adrFile);
		assertTrue(exists);
		
	}
	
	@Test 
	public void testManyADRs() {
		
		// Create a set of test paths, paths of files that should be have been created
		ArrayList<Path> expectedFiles = new ArrayList<Path>();
		ArrayList<String> expectedFileNames = new ArrayList<String>();
		
		for (int id = 0; id < adrNames.length; id++) {
			String name = String.format("%04d", id + 2) + "-" + adrNames[id].replace(' ', '-').toLowerCase() + ".md";
			Path path  = fileSystem.getPath(rootPathName, docsPath, name);
			expectedFiles.add(path);
			expectedFileNames.add(path.toString());
		}
		
		// And now add on the ADR created during initialization
		Path initADR = fileSystem.getPath(rootPathName, docsPath,  "0001-record-architecture-decisions.md");
		expectedFiles.add(initADR);
		expectedFileNames.add(initADR.toString());
		
		
		for (String adrName: adrNames) {
			// Convert the name to an array of args - including the command.
			String[] args = ("new" + " " + adrName).split(" ");
			try {
				adr.run(args, env);
			} catch (ADRException e) {
				// TODO Auto-generated catch block
				fail(e.getMessage());
			}
		}
		
		
		// Check to see if the names exist 
		Path docsDir = fileSystem.getPath(rootPathName, docsPath);
		//TestUtilities.ls(docsDir);
				
		try {
			
			Stream<String> actualFileNamesStream = Files.list(docsDir).map(Path::toString);
			List<String>  actualFileNames = actualFileNamesStream.collect(Collectors.toList());
			   
			assertTrue("File(s) missing", actualFileNames.containsAll(expectedFileNames));
			assertTrue("Unexpected file(s) found", expectedFileNames.containsAll(actualFileNames));
			
		
	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			fail(e.getMessage());
		}
		
	
		
	}
	
    @Ignore("Ignored until code in place")	
    @Test
	public void testSuperseded() {
    		    		
    		// Create a set of test paths, paths of files that should be have been created
    		ArrayList<Path> expectedFiles = new ArrayList<Path>();
    		ArrayList<String> expectedFileNames = new ArrayList<String>();
    		
    		for (int id = 0; id < adrNames.length; id++) {
    			String name = String.format("%04d", id + 2) + "-" + adrNames[id].replace(' ', '-').toLowerCase() + ".md";
    			Path path  = fileSystem.getPath(rootPathName, docsPath, name);
    			expectedFiles.add(path);
    			expectedFileNames.add(path.toString());
    		}
    		
    		// And now add on the ADR created during initialization
    		Path initADR = fileSystem.getPath(rootPathName, docsPath,  "0001-record-architecture-decisions.md");
    		expectedFiles.add(initADR);
    		expectedFileNames.add(initADR.toString());
    		
    		
    		for (String adrName: adrNames) {
    			// Convert the name to an array of args - including the command.
    			String[] args = ("new" + " " + adrName).split(" ");
    			try {
    				adr.run(args, env);
    			} catch (ADRException e) {
    				// TODO Auto-generated catch block
    				fail(e.getMessage());
    			}
    		}
    		
    		// Now create a new ADR that supersedes ADR 5.
    		int supersededId = 5;
    		String newADRTitle = "This superceeds number 5";
    		ArrayList<String> argList = new ArrayList<String>(); 
    		argList.add("new");
    		argList.add("-s");
    		argList.add(Integer.toString(supersededId));
    		argList.addAll(new ArrayList<String>(Arrays.asList((newADRTitle).split(" "))));
    		
    		String[] args = {}; 
    		args = argList.toArray(args);
			try {
				adr.run(args, env);
			} catch (ADRException e) {
				// TODO Auto-generated catch block
				fail(e.getMessage());
			}
    	
			// Check that the the new record mentions that it supersedes ADR 5
			int supersededADRID = 5;
			int newADRID = adrNames.length + 2;
			String newADRFileName = String.format("%04d", newADRID) + "-" + newADRTitle.replace(' ', '-').toLowerCase() + ".md";
			Path newADRFile = fileSystem.getPath(rootPathName, docsPath, newADRFileName);
			long count = 0;
			try {
				count = Files.lines(newADRFile).filter(s -> s.contains("Supersedes the architecture decision record "  + supersededADRID)).count();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				fail(e.getMessage());
			}
			
			assertEquals("The new ADR does not reference the superseded ADR in the text.", count, 1);
			
			
					
			count = 0;
			try {				
				
				Path supersededADRFile = fileSystem.getPath("/project/adr/docs/adr/0005-to-be-superseded.md");
									
				//Files.lines(supersededADRFile).forEach(System.out::println);
				
				count = Files.lines(supersededADRFile).filter(s -> s.contains("Superseded by the architecture decision record "  + newADRID)).count();
				
				assertEquals("The superseded ADR does not reference the ADR that superseded it in the text.", count, 1);
				
			}
			catch (IOException e) {
				fail(e.getMessage());
			}
			
			
			
			
		}

}
