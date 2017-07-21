package org.doble.adr;

import static org.junit.Assert.*;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.*;
import org.junit.runners.MethodSorters;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CommandNewLinkTest {
	final static private String rootPathName = "/project/adr";
	final static private String docsPath = "/doc/adr";
	
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
				.editorRunner(new TestEditorRunner())
				.build();

		// Set up the directory structure
		adr = new ADR(env);

		String[] args = {"init"};
		try {
			adr.run(args);
		} catch (ADRException e) {
			fail("ADR Exception raised");
		}

	
	}


	@After
	public void tearDown() throws Exception {
		fileSystem.close();
	}
	
	@Test
	public void test1Links() {
		// Create some ADRs
		try {
			adr.run(TestUtilities.argify("new An ADR"));
			adr.run(TestUtilities.argify("new Yet another adr"));
			adr.run(TestUtilities.argify("new This ADR is going to be linked to"));  // ADR id 4
			adr.run(TestUtilities.argify("new And even more decisions"));
			adr.run(TestUtilities.argify("new Decisions decisions decisions"));

			// Create new ADR that links to another 
			adr.run(TestUtilities.argify("new -l \"4:Links to:Is linked to from\" Links to number 4"));

			// Now check if the link messages has been added to the new ADR 7)
			Path newADRFile  = fileSystem.getPath(rootPathName, docsPath, "0007-links-to-number-4.md");

			assertTrue(TestUtilities.contains("Links to [ADR 4](0004-this-ADR-is-going-to-be-linked-to.md)", newADRFile));

			// Now check that the link message has been added to the target ADR
			Path targetADRFile = fileSystem.getPath(rootPathName, docsPath, "0004-this-ADR-is-going-to-be-linked-to.md");

			assertTrue(TestUtilities.contains("Is linked to from [ADR 7](0007-links-to-number-4.md)", targetADRFile));

		}
		catch (Exception e)  {
			fail(e.getMessage());
		}




	}

	
	@Test(expected=ADRException.class)
	public void test2MissingLInkSpec() throws ADRException {
		// Create new ADR that links to another 
		adr.run(TestUtilities.argify("new -l Links to number 4"));
	}
	
	

}
