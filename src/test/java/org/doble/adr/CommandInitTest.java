package org.doble.adr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import picocli.CommandLine;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class CommandInitTest {
	private static FileSystem fileSystem;
	final static private String rootPath = "/project/adr";
	final static private String templateDirectoryName = "/dev/templates/";
	final static private String templateFileName = templateDirectoryName + "my_adr_template.md";
	final static private String initTemplateFileName = templateDirectoryName + "my_init_template.md";


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
				.userDir(rootPath)
				.editorCommand("dummyEditor")
				.build();

		// Create a test version of an init template file
		String initTemplateFileContent = "ADR {{id}}: {{name}}\n"
				+ "Date:{{date}}\n"
				+ "Status:{{status}}";
		TestUtilities.createTemplateFile(env.fileSystem, initTemplateFileName, initTemplateFileContent);

		// Create a test version of the template file
		String templateFileContent = "ADR {{id}}: {{name}}\n"
				+ "Date:{{date}}\n"
				+ "Status:{{status}}"
				+ "* Links"
				+ "{{{link.id}}}";
		TestUtilities.createTemplateFile(env.fileSystem, templateFileName, templateFileContent);	
	}

	@AfterEach
	public void tearDown() throws Exception {
		fileSystem.close();
	}

	@Test
	public void testInit() throws Exception {
		String[] args = {"init"};

		ADR.run(args, env);

		// Check to see if the .adr directory has been created.
		String pathName = rootPath + "/.adr";
		Path p = fileSystem.getPath(pathName);
		boolean exists = Files.exists(p);
		assertTrue(exists);

		//Now see if the  standard docs directory has been created
		pathName = rootPath + "/doc/adr";
		p = fileSystem.getPath(pathName);
		exists = Files.exists(p);
		assertTrue(exists);

		// Check if the ADR has been created
		pathName = rootPath + "/doc/adr/0001-record-architecture-decisions.md";
		p = fileSystem.getPath(pathName);
		exists = Files.exists(p);
		assertTrue(exists);

		// Do a sample check on the content
		pathName = rootPath + "/doc/adr/0001-record-architecture-decisions.md";
		p = fileSystem.getPath(pathName);
		List<String> contents = Files.readAllLines(p);

		// Sample the contents
		//TODO refactor this with the use of templates
		int matches = 0;
		for (String line : contents) {
			if (line.contains("Record architecture decisions")) matches++;
//			if (line.contains("## Decision")) matches++;
//			if (line.contains("Nygard")) matches++;
		}

		assertTrue(matches == 1);
	}

	@Test
	public void testInitCustomDirectory() throws Exception {
		String customDir = "myStuff/myDocs/myADRs";

		String[] args = {"init", customDir};

		ADR.run(args, env);

		// Check to see if the custom directory has been created. 
		String pathName = rootPath + "/" + customDir;
		Path p = fileSystem.getPath(pathName);
		boolean exists = Files.exists(p);
		assertTrue(exists);
	}
	
	

	/**
	 * Test to see if a re initialization of the directory causes error code to be given
	 */
	@Test
	public void testReInit() throws Exception {
		int errorCode;

		// Initialize the ADR directories
		String[] args = {"init"};

		 errorCode = ADR.run(args, env);
		
		assertTrue(errorCode == 0);

		// Re-initialize to see if an error code is given  is raised
	
		errorCode = 	ADR.run(args, env);
		
		assertTrue(errorCode == ADR.ERRORGENERAL);
			
	}
	
	//TODO test for other error codes. 

	/**
	 * Test to see if the init command still goes through even
	 * if the EDITOR or VISUAL environment variable has not been set.
	 */
	@Test
	public void testEditorNotSet() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream testErr = new PrintStream(baos);
		// Set up the environment with a null editor command and catch the output
		Environment envWithoutEditor = new Environment.Builder(fileSystem)
				.out(env.out)
				.err(testErr)
				.in(env.in)
				.userDir(env.dir)
				.editorCommand(null)
				.build();

		String[] args = {"init"};

		int exitCode = ADR.run(args, envWithoutEditor);
		
		assertEquals(ADR.ERRORENVIRONMENT, exitCode, "Exit code  does not indicate that init has errors");
		String commandOutput = new String(baos.toByteArray());
		assertTrue(commandOutput.contains("WARNING"), "No warning given from init command that edit has not been set.");

		// Check to see if the .adr directory has been created even though an error is there .
		String pathName = rootPath + "/.adr";
		Path p = fileSystem.getPath(pathName);
		boolean exists = Files.exists(p);
		assertTrue(exists);
	}
	
	
	@Test
	public void testInitWithTemplate() throws Exception {
		String expectedTemplate = "/dev/templates/my_adr_template.md";
		String[] args = {"init", "-t", expectedTemplate};

		ADR.run(args, env);
		
		// Now check to see if the properties files (.adr) has been 
		// created with the template file property. 
        ADRProperties properties = new ADRProperties(env);
        properties.load();
        
       String actualTemplate = properties.getProperty("templateFile").replace('\\', '/');  // Convert to unix delimitors
                         
       assertEquals(expectedTemplate, actualTemplate);
		
		
	}
	
	@Test
	public void testInitWithTemplateAndInitTemplate() {
		
		// Now run the command using templates specified in setup
		String[] args = {"init", "-template", templateFileName, "-i", initTemplateFileName};
		ADR.run(args, env);
		
		// Now check to see if the properties files (.adr) has been 
		// created with the template file property. 
		ADRProperties properties = new ADRProperties(env);
		try {
			properties.load();
		} catch (ADRException e) {
			fail(e.getMessage());
		}
		String actualTemplate = properties.getProperty("templateFile").replace('\\', '/');  // Convert to Unix-style delimiters
		assertEquals(templateFileName, actualTemplate);
		
		// Check to see if the initial template has been set in the properties file
		String actualInitTemplateFileName = properties.getProperty("initialTemplateFile").replace('\\', '/');  // Convert to unix delimitors
		assertEquals(initTemplateFileName, actualInitTemplateFileName);
		
		// Check to see if the initial template has been set up 
		String expectedInitADRFileContent = "ADR 1: Record architecture decisions\n"
				+ "Date:" + DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now()) + "\n"
				+ "Status:Proposed";
		Path docsPath = env.dir.resolve(properties.getProperty("docPath"));
		Path initialADRFile = docsPath.resolve("0001-record-architecture-decisions.md");
		 
		assertTrue(Files.exists(initialADRFile));	
		
		// Now check the contents
		String actualInitADRFileContent = "";
		try {
			actualInitADRFileContent = Files.lines(initialADRFile).collect(Collectors.joining("\n"));
		} catch (IOException e) {
			fail(e.getMessage());
		}
		assertEquals(expectedInitADRFileContent, actualInitADRFileContent);
	}


	
	
	@Test
	public void testInitWithInitTemplateAlone() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);

		Environment env = new Environment.Builder(fileSystem)
				.out(System.out)
				.err(ps)
				.in(System.in)
				.userDir(rootPath)
				.editorCommand("dummyEditor")
				.build();
		
		String[] args = {"init",  "-initial", initTemplateFileName};
		int errorCode = ADR.run(args, env);
		
		assertEquals(CommandLine.ExitCode.USAGE, errorCode);

		// read the output
		String content = new String(baos.toByteArray());

		assertTrue(content.length() > 0);

		assertTrue(content.contains("ERROR"));  //At least this is shown
		assertTrue(content.contains("[INITIALTEMPLATE]")); //At least this is shown
		
	}
	
	@Test
	public void testTemplateFileNotFound() {
		String nonExistingTemplate = "/dev/mytemplates/non-existing_adr_template.md";
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);

		Environment env = new Environment.Builder(fileSystem)
				.out(System.out)
				.err(ps)
				.in(System.in)
				.userDir(rootPath)
				.editorCommand("dummyEditor")
				.build();
		
		String[] args = {"init", "-t", nonExistingTemplate};
		
		int exitCode = ADR.run(args, env);
		
		assertEquals(exitCode, 2);
		
		String commandOutput = new String(baos.toByteArray());
		assertTrue(commandOutput.contains("ERROR"), "No error given from init command that template file does not exist.");


	}
	
	@Test
	public void testInitialTemplateFileNotFound() {
		String nonExistingIitialTemplate = "/dev/mytemplates/non-existing_adr_initial_template.md";
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);

		Environment env = new Environment.Builder(fileSystem)
				.out(System.out)
				.err(ps)
				.in(System.in)
				.userDir(rootPath)
				.editorCommand("dummyEditor")
				.build();
		
		String[] args = {"init", "-t", templateFileName, "-i", nonExistingIitialTemplate};
		
		
     	int exitCode = ADR.run(args, env);
		
		assertEquals(exitCode, 2);
		
		String commandOutput = new String(baos.toByteArray());
		assertTrue(commandOutput.contains("ERROR"), "No error given from init command that initial template file does not exist.");


	}
	
	
	
}
