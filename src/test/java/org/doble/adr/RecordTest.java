package org.doble.adr;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import org.doble.adr.model.Record;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RecordTest {
	private FileSystem fileSystem;
	private Path docPath = null;
	private DateTimeFormatter dateFormatter;

	@BeforeEach
	public void setUp() throws Exception {
		//Path rootPath = null;

		fileSystem = Jimfs.newFileSystem(Configuration.unix());

		docPath = fileSystem.getPath("/test");

		Files.createDirectory(docPath);

		dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
	}

	@AfterEach
	public void tearDown() throws Exception {
		fileSystem.close();
	}

	@Test
	@Order(1)
	public void test1BasicRecordConstruction() throws Exception {
		String expectedContents = "# 7. This is a new record\n" + 
				"\n" + 
				"Date: {{date}}\n" + 
				"\n" + 
				"## Status\n" + 
				"\n" + 
				"Proposed\n" + 
				"\n\n" + 
				"## Context\n" + 
				"\n" + 
				"Record the architectural decisions made on this project.\n" + 
				"\n" + 
				"## Decision\n" + 
				"\n" + 
				"We will use Architecture Decision Records, as described by Michael Nygard in this article: http://thinkrelevance.com/blog/2011/11/15/documenting-architecture-decisions\n" + 
				"\n" + 
				"## Consequences\n" + 
				"\n" + 
				"See Michael Nygard's article, linked above.";
		
		expectedContents = expectedContents.replace("{{date}}", DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now()));

		// Build the record
		Record record = new Record.Builder(docPath, dateFormatter).id(7).name("This is a new record").build();

		record.createPeristentRepresentation();

		// Check if the ADR file has been created
		assertTrue(Files.exists(fileSystem.getPath("/test/0007-this-is-a-new-record.md")));

		// Read in the file
		Path adrFile = fileSystem.getPath("/test/0007-this-is-a-new-record.md");
		Stream<String> lines = Files.lines(adrFile);
		String actualContents = lines.collect(Collectors.joining("\n"));
		lines.close();

        assertEquals(expectedContents, actualContents);

	}



	@Test
	@Order(2)
	public void test2ComplexRecordConstruction() throws Exception {
		LocalDate date = LocalDate.now();
		
		String expectedContents = "# 42. This is a complex record\n" + 
				"\n" + 
				"Date: {{date}}\n" + 
				"\n" + 
				"## Status\n" + 
				"\n" + 
				"Accepted\n" + 
				"\n" + 
				"\n" + 
				"## Context\n" + 
				"\n" + 
				"Record the architectural decisions made on this project.\n" + 
				"\n" + 
				"## Decision\n" + 
				"\n" + 
				"We will use Architecture Decision Records, as described by Michael Nygard in this article: http://thinkrelevance.com/blog/2011/11/15/documenting-architecture-decisions\n" + 
				"\n" + 
				"## Consequences\n" + 
				"\n" + 
				"See Michael Nygard's article, linked above.";
		expectedContents = expectedContents.replace("{{date}}", DateTimeFormatter.ISO_LOCAL_DATE.format(date));
		
		Record record = new Record.Builder(docPath, dateFormatter).id(42)
				.name("This is a complex record")
				.date(date)
				.status("Accepted")
				.build();
		record.createPeristentRepresentation();

		// Check if the ADR file has been created
		assertTrue(Files.exists(fileSystem.getPath("/test/0042-this-is-a-complex-record.md")));

		// Read in the file
		Path adrFile = fileSystem.getPath("/test/0042-this-is-a-complex-record.md");
		Stream<String> lines = Files.lines(adrFile);
		String actualContents = lines.collect(Collectors.joining("\n"));
		lines.close();
	
		assertEquals(expectedContents, actualContents);

	}

	@Test
	@Order(3)
	public void nameIsLowerCased() throws Exception {

		Record record = new Record.Builder(docPath, dateFormatter).id(8).name("CDR is stored in a relational database").build();

		record.createPeristentRepresentation();

		// Check if the ADR file has been created
		assertTrue(Files.exists(fileSystem.getPath("/test/0008-cdr-is-stored-in-a-relational-database.md")));
	}
	
	/**
	 * Tests link construction without the addition of link meta information (as comments) embedded in the ADR. 
	 * @throws Exception
	 */
	@Test
	@Order(4)
	public void testLinkConstruction() throws Exception {
		
		// Create some ADR files that are going to be linked to.
		Path adrTestFilePath = docPath.resolve("0004-linked-to.md");
		Files.createFile(adrTestFilePath);
		adrTestFilePath = docPath.resolve("0005-also-linked-to.md");
		Files.createFile(adrTestFilePath);
		
	
		
		Record record = new Record.Builder(docPath, dateFormatter).id(102).name("Contains some links").build();
        record.createPeristentRepresentation();
        
		// <target_adr>:<link_description>
		record.addLink("4:Links to");
		record.addLink("5:Also links to");
				
		record.createPeristentRepresentation();
		
		// Now check that the links have been added
		Path adrFile = fileSystem.getPath("/test/0102-contains-some-links.md");
	    Stream<String> lines = Files.lines(adrFile);
	    
	    //boolean result = lines.anyMatch("* Links to [ADR 4](004-contains-some-links.md)"::equals);
	    String contents = lines.collect(Collectors.joining("\n"));
		lines.close();
		
		assertTrue(contents.contains("* Links to [ADR 4](0004-linked-to.md)"));
		assertTrue(contents.contains("* Also links to [ADR 5](0005-also-linked-to.md)"));
		
	}

	@Test
	@Order(5)
	public void testRecordConstructionWithDefaultAuthor() throws Exception {
		String expectedContents = "# 66. This is a new record with default author\n" +
				"\n" +
				"Author: {{author}}";

		expectedContents = expectedContents.replace("{{author}}", System.getProperty("user.name"));

		// Build the record
		Record record = new Record.Builder(docPath, dateFormatter)
				.id(66)
				.name("This is a new record with default author")
				.template("rsrc:template_with_author.md")
				.build();

		record.createPeristentRepresentation();

		// Check if the ADR file has been created
		assertTrue(Files.exists(fileSystem.getPath("/test/0066-this-is-a-new-record-with-default-author.md")));

		// Read in the file
		Path adrFile = fileSystem.getPath("/test/0066-this-is-a-new-record-with-default-author.md");
		Stream<String> lines = Files.lines(adrFile);
		String actualContents = lines.collect(Collectors.joining("\n"));
		lines.close();

		assertEquals(expectedContents, actualContents);

	}

	@Test
	@Order(6)
	public void testRecordConstructionWithGivenAuthor() throws Exception {
		String expectedContents = "# 67. This is a new record with given author\n" +
				"\n" +
				"Author: Andrew Doble";

		// Build the record
		Record record = new Record.Builder(docPath, dateFormatter)
				.id(67)
				.name("This is a new record with given author")
				.author("Andrew Doble")
				.template("rsrc:template_with_author.md")
				.build();

		record.createPeristentRepresentation();

		// Check if the ADR file has been created
		assertTrue(Files.exists(fileSystem.getPath("/test/0067-this-is-a-new-record-with-given-author.md")));

		// Read in the file
		Path adrFile = fileSystem.getPath("/test/0067-this-is-a-new-record-with-given-author.md");

		Stream<String> lines = Files.lines(adrFile);
		String actualContents = lines.collect(Collectors.joining("\n"));
		lines.close();

		assertEquals(expectedContents, actualContents);

	}
	
	@Test
	@Order(7)
	public void testExplicitDateFormatter() throws Exception {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

		String expectedContents = "Date: {{date}}";

		expectedContents = expectedContents.replace("{{date}}", dateFormatter.format(LocalDate.now()));

		// Build the record
		Record record = new Record.Builder(docPath, dateFormatter)
				.id(77)
				.name("Only date")
				.template("rsrc:template_only_date.md")
				.build();

		record.createPeristentRepresentation();

		// Check if the ADR file has been created
		assertTrue(Files.exists(fileSystem.getPath("/test/0077-only-date.md")));

		// Read in the file
		Path adrFile = fileSystem.getPath("/test/0077-only-date.md");
		Stream<String> lines = Files.lines(adrFile);
		String actualContents = lines.collect(Collectors.joining("\n"));
		lines.close();

		assertEquals(expectedContents, actualContents);

	}
	
	
	
	@Test
	@Order(8)
	public void testLinkConstructionWithTemplate() throws Exception
	{
		String name = "Target expected";
		
		// Create some ADR files that are going to be linked to.
		Path adrTestFilePath = docPath.resolve("0001-first-link-target-expected.md");
		Files.createFile(adrTestFilePath);
		adrTestFilePath = docPath.resolve("0002-second-link-target-expected.md");
		Files.createFile(adrTestFilePath);
	
		Record record = new Record.Builder(docPath, dateFormatter)
				                  .id(3).
				                  name(name)
				                  .template("rsrc:template_link.md")
				                  .build();
        //record.store();
        
		// <target_adr>:<link_description>
		record.addLink("1:See also");
		record.addLink("2:See also");
				
		Path adrPath = record.createPeristentRepresentation();
		
		// Now check that the generated ADR matches the expected contents
		Stream<String> actualLines = Files.lines(adrPath);
		String actualContents = actualLines.collect(Collectors.joining("\n"));
		actualLines.close();
		

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		//URL url = classLoader.getResource("test_link/markdown/0001-target-expected.md"); // no leading slash
				
		InputStream inputStream  = classLoader.getResourceAsStream("test_link/markdown/0003-link-source-expected.md");
		
		assertNotNull(inputStream);
		
		
		StringBuilder expectedContentsBuilder = new StringBuilder();
		 
	    try (Reader reader = new BufferedReader(new InputStreamReader(inputStream))) {
	  	        int c = 0;
	        while ((c = reader.read()) != -1) {
	        	if (c != '\r') {  // Remove windows based returns
	        		expectedContentsBuilder.append((char) c);
	        	}
	        }
	    }
		// Check that the created ADR has referenced the other ADRS and the meta-data has been correctly added. 
		assertEquals(expectedContentsBuilder.toString(), actualContents);
		
		
	}
	
	@Disabled
	@Test
	@Order(9)
	void testReverseLinks() {
		fail("Not yet implemented");
		//TODO implement this 
	}
	
	@Disabled
	@Test
	@Order(10)
	void testUpdatingLinks()  {
		fail("Not yet implmented");
		//TODO implement this
	}
		
}
