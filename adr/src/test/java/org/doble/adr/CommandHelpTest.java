/**
 * 
 */
package org.doble.adr;

import static org.junit.Assert.*;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

// Import a mock of the file systes
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

/**
 * @author adoble
 *
 */
public class CommandHelpTest {
    private static FileSystem fileSystem;
    private final String rootPath = "/project/adr";
	

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Set up the mock file system
		fileSystem = Jimfs.newFileSystem(Configuration.unix());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void test() {
		ADR adr = new ADR();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		
		Environment env = new Environment.Builder(fileSystem)
				.out(ps)
				.err(System.err)
				.in(System.in)
				.userDir(rootPath)
				.build();
		
		
		String[] args = {"help"};
		
			try {
				adr.run(args, env);   //TODO env --> ADR constructor
			} catch (ADRException e) {
				fail("ADR Exception raised: " + e.getMessage());
			}
			
		
		
		// read the output
		String content = new String(baos.toByteArray());
		
		assertTrue(content.length() > 0);
		
		assertTrue(content.contains("Help"));  //At least this command is shown
		
	}

}
