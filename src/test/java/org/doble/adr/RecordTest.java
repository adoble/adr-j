package org.doble.adr;

import static org.junit.Assert.*;

import java.nio.file.*;
import java.text.DateFormat;
import java.util.Date;

import org.junit.*;
import org.junit.runners.*;


import org.hamcrest.CoreMatchers.*;
import org.junit.Assert.*;

import com.google.common.jimfs.*;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RecordTest {
	private  FileSystem fileSystem;
	private Path docPath = null;
	
	@Before
	public void setUp() throws Exception {
		Path rootPath = null; 
		
		// Set up the mock file system
		try {
			fileSystem = Jimfs.newFileSystem(Configuration.unix());

			docPath = fileSystem.getPath("/test");

			Files.createDirectory(docPath);
		} catch (Exception e) {
			e.printStackTrace();
		}

	
	}

	@After
	public void tearDown() throws Exception {
		fileSystem.close();
	}

	@Test
	public void test1BasicRecordConstruction() {
				
		Record record = new Record.Builder(docPath).id(7).name("This is a new record").build();
		
		try {
			record.store();
			
			
			// Check if the ADR file has been created
			assertTrue(Files.exists(fileSystem.getPath("/test/0007-this-is-a-new-record.md"))); 
			
			// Read in the file
			Path adrFile = fileSystem.getPath("/test/0007-this-is-a-new-record.md");
			
			assertTrue(TestUtilities.contains("0007", adrFile));
			assertTrue(TestUtilities.contains("This is a new record", adrFile));
			
			// Check the default values
			assertTrue(TestUtilities.contains("Accepted", adrFile));
			assertTrue(TestUtilities.contains("Record the architectural decisions made on this project.", adrFile));
			assertTrue(TestUtilities.contains("We will use Architecture Decision Records, as described by Michael Nygard in this article: http://thinkrelevance.com/blog/2011/11/15/documenting-architecture-decisions", adrFile));
			assertTrue(TestUtilities.contains("See Michael Nygard's article, linked above.", adrFile));
		} catch (ADRException e) {
			fail("ADR Exception: " + e.getMessage());
		} catch (Exception e) {
			fail("Exception: " + e.getMessage());
		}
		
		
	}
	
	@Test
	public void test2ComplexRecordConstruction() {
		Date date = new Date();
		
		Record record = new Record.Builder(docPath).id(42)
								  .name("This is a complex record")
				                  .date(date)
				                  .status("Accepted")
				                  .context("The context,")
				                  .decision("The decision.")
				                  .consequences("What it implies,")
				                  .build(); 
		try {
			record.store();
			
			// Check if the ADR file has been created
			assertTrue(Files.exists(fileSystem.getPath("/test/0042-this-is-a-complex-record.md"))); 
			
			// Read in the file
			Path adrFile = fileSystem.getPath("/test/0042-this-is-a-complex-record.md");
						
			assertTrue(TestUtilities.contains("0042", adrFile));
			assertTrue(TestUtilities.contains("This is a complex record", adrFile));
			assertTrue(TestUtilities.contains(DateFormat.getDateInstance().format(date), adrFile));
			assertTrue(TestUtilities.contains("Accepted", adrFile));
			assertTrue(TestUtilities.contains("The decision.", adrFile));
			assertTrue(TestUtilities.contains("What it implies,", adrFile));
		} catch (ADRException e) {
			fail("ADRException: " + e.getMessage());
		} catch (Exception e) {
			fail("Exception: " + e.getMessage());
		}
		
							
	}



}
