package org.doble.adr;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.stream.Stream;

import org.doble.adr.ADR;
import org.doble.adr.ADRException;
import org.doble.adr.Environment;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

public class CommandNewTest {
	private static FileSystem fileSystem;
    final static private String rootPath = "/project/adr";
    final static private String docsPath = "/docs/adr";
    
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
		// Set up the mock file system
		try {
			fileSystem = Jimfs.newFileSystem(Configuration.unix());

			Path rootPath = fileSystem.getPath("/project");

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
				.userDir(rootPath)
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
		String fileName = ("0001 " + adrName +".md").replace(' ',  '-').toLowerCase();
		Path adrFile = fileSystem.getPath(rootPath, docsPath, fileName);
		
		
		boolean exists = Files.exists(adrFile);
		assertTrue(exists);
		
	}

}
