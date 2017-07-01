/**
 * 
 */
package org.doble.adr;

import static org.junit.Assert.*;

import java.io.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author adoble
 *
 */
public class CommandHelpTest {
    PrintStream out = new PrintStream(new ByteArrayOutputStream());
    PrintStream originalOut; 

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		System.setOut(this.originalOut);
	}

	@Test
	public void test() {
		
		assertTrue(true); 
		
//		ADR.main(new String[]{"help"});
//		
//		assertTrue(out.toString().length() > 0 );
//		
		
	}

}
