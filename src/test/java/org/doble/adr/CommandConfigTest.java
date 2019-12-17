package org.doble.adr;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;


import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;


public class CommandConfigTest {
	final static private String rootPathName = "/project/adr";
	private static FileSystem fileSystem;
	private Environment env;
	
	@BeforeEach
	public void setUp() throws Exception {
		
		// Set up the mock file system
		fileSystem = Jimfs.newFileSystem(Configuration.unix());

		Path projectPath = fileSystem.getPath("/project");
		Files.createDirectory(projectPath);

		// Set up the environment
		env = new Environment.Builder(fileSystem)
				.out(System.out)
				.err(System.err)
				.in(System.in)
				.userDir(rootPathName)
				.editorCommand("dummyEditor")
				.build();

		// Initialise up the directory structure
				String[] args = {"init"};
				int exitCode = ADR.run(args, env);
				assertEquals(exitCode,  0); 	}

	@AfterEach
	public void tearDown() throws Exception {
		fileSystem.close();
	}
	
	@Test
	public void testConfig() throws Exception {
		//Catch the output 
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream testOut = new PrintStream(baos);
		Environment localEnv = new Environment.Builder(fileSystem)
				.out(testOut)
				.err(System.err)
				.in(System.in)
				.userDir(rootPathName)
				.editorRunner(new TestEditorRunner())
				.build();

		
		int exitCode = ADR.run(TestUtilities.argify("config"), localEnv);
		assertEquals(exitCode, 0);
		
		String list = new String(baos.toByteArray());
		assertTrue(list.contains("docPath=doc/adr"));
		

	}
	
	@Test 
	void testConfigAuthorSingleName() throws Exception {
		
		int exitCode = ADR.run(TestUtilities.argify("config author doble"), env);
		assertEquals(0, exitCode);
		
		ADRProperties properties = new ADRProperties(env);
		properties.load();

		assertEquals("doble", properties.getProperty("author"));
	}
	
	
	@Test 
	void testConfigAuthorMulitpleName() throws Exception {
		
		int exitCode = ADR.run(TestUtilities.argify("config author William Shakespeare the Bard"), env);
		assertEquals(0, exitCode);
		
		ADRProperties properties = new ADRProperties(env);
		properties.load();

		assertEquals("William Shakespeare the Bard", properties.getProperty("author"));
	}
	
	@Test
	void testDocsPath() throws Exception {
		int exitCode = ADR.run(TestUtilities.argify("config docsPath documents/ADRs"), env);
		assertEquals(0, exitCode);
		
		ADRProperties properties = new ADRProperties(env);
		properties.load();
		
		assertTrue(properties.getProperty("docsPath").equalsIgnoreCase("documents/ADRs"));
		
	}
	
	@Test 
	void testDateFormat() throws Exception {
		// Check that the default data format is set to ISO_LOCAL_DATE
		ADRProperties properties = new ADRProperties(env);
		properties.load();
		assertTrue(properties.getProperty("dateFormat").equals("ISO_LOCAL_DATE"), "Default date format is ISO_LOCAL_DATE");
		
		
		int exitCode = ADR.run(TestUtilities.argify("config dateFormat ISO_ORDINAL_DATE"), env);
		assertEquals(0, exitCode);
		
		properties = new ADRProperties(env);
		properties.load();
		
		assertTrue(properties.getProperty("dateFormat").equals("ISO_ORDINAL_DATE"));
		
	
		
		
		
	}
	
	@Test
	void testIncorrectDataFormat() throws Exception {

		ADRProperties properties = new ADRProperties(env);
		properties.load();
		String currentDateFormat = properties.getProperty("dateFormat");

		int exitCode = ADR.run(TestUtilities.argify("config dateFormat INCORRECT_DATE_FORMAT"), env);
		assertEquals(2, exitCode);

		// The dateFormat should not have been changed when an incorrect format has been specified. 
		properties.load();
		assertEquals(currentDateFormat, properties.getProperty("dateFormat"));
	}
	
	@Test
	void testTemplateFile() throws Exception {
		  
		int exitCode = ADR.run(TestUtilities.argify("config templateFile /usr/templates/project_template.adoc"), env);
		assertEquals(0, exitCode);

		ADRProperties properties = new ADRProperties(env);
		properties.load();
		assertEquals("/usr/templates/project_template.adoc", properties.getProperty("templateFile"));
	}
	

}
