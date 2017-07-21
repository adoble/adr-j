package org.doble.adr;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

public class CommandListTest {
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
	public void testList() {
		
		String[] testData = {
				"new An ADR",
				"new Yet another adr",
				"new This ADR is going to be linked to",
				"new And even more decisions",
				"new Decisions decisions decisions"
		};
				
		
		
		
		// Create some ADRs
		try {
			for (int i = 0; i < testData.length ; i++) {
				adr.run(TestUtilities.argify(testData[i]));
			}
		}
		catch (Exception e)  {
			fail(e.getMessage());
		}
		
		//Catch the output 
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream testOut =  new PrintStream(baos);
		Environment localEnv = new Environment.Builder(fileSystem)
				.out(testOut)
				.err(System.err)
				.in(System.in)
				.userDir(rootPathName)
				.editorRunner(new TestEditorRunner())
				.build();
		
		ADR localADR = new ADR(localEnv);
		
		try {
			localADR.run(TestUtilities.argify("list"));
		}
		catch (Exception e)  {
			fail(e.getMessage());
		}
		
		
		String[] expectedFiles = {
				"0002-an-ADR.md",
				"0003-yet-another-adr.md",
				"0004-this-ADR-is-going-to-be-linked-to.md",
				"0005-and-even-more-decisions.md",
				"0006-decisions-decisions-decisions.md"
		};
		
		
		String list = new String(baos.toByteArray());
		for (String expected: expectedFiles) {
			assertTrue(list.contains(expected));
		}
		
		
	}

}
