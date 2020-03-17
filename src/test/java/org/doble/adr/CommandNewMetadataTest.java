package org.doble.adr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import picocli.CommandLine;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;



class CommandNewMetadataTest {
	final static private String rootPathName = "/project/adr";
	final static private String docsPath = "/doc/adr";

	private static FileSystem fileSystem;

	private Environment env;

	@BeforeEach
	void setUp() throws Exception {
		Path rootPath = null;

		// Set up the mock file system
		fileSystem = Jimfs.newFileSystem(Configuration.unix());

		rootPath = fileSystem.getPath("/project");

		Files.createDirectory(rootPath);

        // Now create a  template file in the filesystem 
		// that can handle metadata (i.e. has a template.comment field).
		// This is copied from a resource test file. 
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		
		InputStream inStream  = classLoader.getResourceAsStream("metadata_test_template.md");
		BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
		
		Path templateDirectory = fileSystem.getPath("/project/templates");
		Files.createDirectory(templateDirectory);
		
		Path templatePath = templateDirectory.resolve("template.md");
		
		BufferedWriter writer = Files.newBufferedWriter(templatePath);
		
		String contentLine = "";
		while ((contentLine = reader.readLine()) != null) {
			writer.write(contentLine + "\n");
		}
        reader.close();
        writer.close();
		


		// Set up the environment
		env = new Environment.Builder(fileSystem)
				.out(System.out)
				.err(System.err)
				.in(System.in)
				.userDir(rootPathName)
				.editorCommand("dummyEditor")
				.editorRunner(new TestEditorRunner())
				.build();

		// Initialise up the directory structure and specify a template
		// that can handle metadata (i.e. as a template.comment field
		assertEquals(ADR.run(TestUtilities.argify("init -t /project/templates/template.md"), env), 0);

	}

	@AfterEach
	void tearDown() throws Exception {
		fileSystem.close();
	}

	/**
	 * Check that link meta data is correctly created
	 */
	@Test 
	public void testLinkMetaData() throws Exception {
		//  Create some ADRs using a template with a {{template.comment}} field so that meta data is used. 
		assertEquals(ADR.run(TestUtilities.argify("new First Link Target ADR"), env), 0);  // ADR #1
		assertEquals(ADR.run(TestUtilities.argify("new Second Link Target ADR"), env), 0);  // ADR #2
		assertEquals(ADR.run(TestUtilities.argify("new "
				+ "-l \"1:References:Is referenced by\" "
				+ "Link Source ADR"
				), env), 0);  // ADR #3

		Path newADRFile = fileSystem.getPath(rootPathName, docsPath, "0003-link-source-adr.md");
		assertNotNull(newADRFile);
		
		
			
		// Check that the metadata is there 		
		assertTrue(TestUtilities.contains("<!--* {{{link.comment=\"References\"}}} [ADR {{{link.id=\"1\"}}}]({{{link.file=\"0001-first-link-target-adr.md\"}}})-->", newADRFile));

	}
	
	@Test
	@Disabled
	public void testReverseLinksWithMetaData() throws Exception {
		//  Create some ADRs using a template with a {{template.comment}} field so that meta data is used. 
		assertEquals(ADR.run(TestUtilities.argify("new First Link Target ADR"), env), 0);  // ADR #1
		assertEquals(ADR.run(TestUtilities.argify("new Second Link Target ADR"), env), 0);  // ADR #2
		assertEquals(ADR.run(TestUtilities.argify("new "
				+ "-l \"1:References:Is referenced by\" "
				+ "Link Source ADR"
				), env), 0);  // ADR #3

		Path targetADRFile = fileSystem.getPath(rootPathName, docsPath, "0001-first-link-target-adr.md");
		assertNotNull(targetADRFile);

		TestUtilities.printFile(targetADRFile);	
		
		assertTrue(TestUtilities.contains("Is referenced by [ADR 3](0003-link-source-adr.md)", targetADRFile));
	
	}
	
	@Test
	@Disabled
	public void testSupercecedesMetaData() throws Exception {
		 fail("To be implemented");	
	}
	
	/** Test is no links are present then the template data is not shown alone in the ADR
	 * 
	 */
	@Test
	@Disabled
	public void testEmptyLinks() throws Exception {
		 fail("To be implemented");	
	}
	
	/** Test is ADR is not superseded then the template data is not shown alone in the ADR
	 * 
	 */
	@Test
	@Disabled
	public void testEmptySupercedes() throws Exception {
		 fail("To be implemented");	
	}
	
	

}
