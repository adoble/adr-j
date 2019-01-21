package org.doble.adr;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.util.Date;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RecordTest {
	private FileSystem fileSystem;
	private Path docPath = null;

	@BeforeEach
	public void setUp() throws Exception {
		Path rootPath = null;

		fileSystem = Jimfs.newFileSystem(Configuration.unix());

		docPath = fileSystem.getPath("/test");

		Files.createDirectory(docPath);
	}

	@AfterEach
	public void tearDown() throws Exception {
		fileSystem.close();
	}

	@Test
	@Order(1)
	public void test1BasicRecordConstruction() throws Exception {

		Record record = new Record.Builder(docPath).id(7).name("This is a new record").build();

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
	}

	@Test
	@Order(2)
	public void test2ComplexRecordConstruction() throws Exception {
		Date date = new Date();

		Record record = new Record.Builder(docPath).id(42)
				.name("This is a complex record")
				.date(date)
				.status("Accepted")
				.context("The context,")
				.decision("The decision.")
				.consequences("What it implies,")
				.build();
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
	}

	@Test
	@Order(3)
	public void nameIsLowerCased() throws Exception {

		Record record = new Record.Builder(docPath).id(8).name("CDR is stored in a relational database").build();

		record.store();

		// Check if the ADR file has been created
		assertTrue(Files.exists(fileSystem.getPath("/test/0008-cdr-is-stored-in-a-relational-database.md")));
	}

}
