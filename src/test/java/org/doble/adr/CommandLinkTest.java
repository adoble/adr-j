package org.doble.adr;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import picocli.CommandLine;

import org.junit.jupiter.api.*;

//import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CommandLinkTest {
	final static private String rootPathName = "/project/adr";
	final static private String docsPath = "/doc/adr";

	private static FileSystem fileSystem;

	private Environment env;

	@BeforeEach
	public void setUp() throws Exception {
		Path rootPath = null;

		// Set up the mock file system
		fileSystem = Jimfs.newFileSystem(Configuration.unix());

		rootPath = fileSystem.getPath("/project");

		Files.createDirectory(rootPath);

		// Set up the environment
		env = new Environment.Builder(fileSystem)
				.out(System.out)
				.err(System.err)
				.in(System.in)
				.userDir(rootPathName)
				.editorCommand("dummyEditor")
				.editorRunner(new TestEditorRunner())
				.build();

		// Initialize up the directory structure
		
		assertEquals(ADR.run(TestUtilities.argify("init -t rsrc:template_link.md"), env), 0); 
		
		
	}

	@AfterEach
	public void tearDown() throws Exception {
		fileSystem.close();
	}

	/** 
	 * Create some valid links to ADRs and check if they are added. 
	 * @throws Exception
	 */
	@Test
	@Order(1)
	@Disabled
	public void testLinks() throws Exception {
		// Create some ADRs
		assertEquals(ADR.run(TestUtilities.argify("new Target expected"), env), 0);         // ADr id 1
		assertEquals(ADR.run(TestUtilities.argify("new First source expected"), env), 0);   // ADR id 2
		assertEquals(ADR.run(TestUtilities.argify("new Second source expected"), env), 0);  // ADR id 3


		// Link the ADRs
		assertEquals(ADR.run(TestUtilities.argify("link 2 1 -sd \"See also\" -td  \"Referenced by\""), env), 0);

		assertEquals(ADR.run(TestUtilities.argify("link 3 1 -sd \"See also\" -td  \"Referenced by\""), env), 0);
		
				
       // Check the the links are there.
		fail();
		// TODO - but first extend the unit tests for Record with the links
		// TODO - may also need to generally change the command file handling so that file with the protocol rsrc: (resource file) are correctly handled. 
		/* Check if the ADR file has been created
				assertTrue(Files.exists(fileSystem.getPath("/test/0067-this-is-a-new-record-with-given-author.md")));

				// Read in the file
				Path adrFile = fileSystem.getPath("/test/0067-this-is-a-new-record-with-given-author.md");

				Stream<String> lines = Files.lines(adrFile);
				String actualContents = lines.collect(Collectors.joining("\n"));
				lines.close();

				assertEquals(expectedContents, actualContents);
		*/
	}

	
	
}
