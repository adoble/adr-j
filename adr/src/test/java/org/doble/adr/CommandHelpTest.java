/**
 * 
 */
package org.doble.adr;

import static org.junit.Assert.*;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

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
    PrintStream out = new PrintStream(new ByteArrayOutputStream());
    PrintStream err = new PrintStream(new ByteArrayOutputStream());
    PrintStream originalOut; 
    PrintStream originalErr; 
    private static FileSystem fileSystem;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Set up the mock file system
		fileSystem = Jimfs.newFileSystem(Configuration.windows());
		
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
		this.originalOut = System.out;
		System.setOut(this.out);
		
		this.originalErr = System.err;
		System.setErr(this.err);
		
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		System.setOut(this.originalOut);
		System.setErr(this.originalErr);
	}

	@Test
	public void test() {
		ADR.setFileSystem(fileSystem);
		ADR adr = new ADR();
		
		String[] args = {"help"};
		
			try {
				adr.run(args);
			} catch (ADRException e) {
				// TODO Auto-generated catch block
				fail("ADR Exception raised");
			}
			
		
		
		// read the output
		String outData = out.toString();
		
		assertTrue(outData.length() > 0);
		
	}

}
