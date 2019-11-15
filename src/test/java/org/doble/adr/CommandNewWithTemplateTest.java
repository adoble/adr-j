package org.doble.adr;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import picocli.CommandLine;
import picocli.CommandLine.ExitCode;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommandNewWithTemplateTest {
	final static private String rootPathName = "/new_project/adr";
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

		env = new Environment.Builder(fileSystem)
				.out(System.out)
				.err(System.err)
				.in(System.in)
				.userDir(rootPathName)
				.editorCommand("dummyEditor")
				.editorRunner(new TestEditorRunner())
				.build();

	}

	@AfterEach
	public void tearDown() throws Exception {
		fileSystem.close();
	}


	
	/**
	 * Test the new command using different templates
	 * @throws Exception
	 */
	@Test 
	public void testTemplates () throws Exception {
		String templateContent = "# {{id}}. {{name}}\n" + 
				"Date: {{date}}\n" + 
				"## Status\n" + 
				"{{status}}\n" + 
				"## Links\n" + 
				"* {{{link.comment}}} [ADR {{{link.id}}}]({{{link.file}}})\n" + 
				"## Superseded\n" +
				"* Supersedes [ADR {{{superseded.id}}}]({{{superseded.file}}})\n" + 
				"## Content" + 
				"Some general content. ";
		
		String expectedContents = "# 1. ADR created with template\n" + 
				"Date: {{date}}\n" + 
				"## Status\n" + 
				"Proposed\n" + 
				"## Links\n" + 
				"## Superseded\n" +
				"## Content" + 
				"Some general content. ";
		expectedContents = expectedContents.replace("{{date}}", DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now()));
		
		// Create  a template
		TestUtilities.createTemplateFile(env.fileSystem, "/usr/adoble/templates/template.md", templateContent);
	    assertTrue(Files.exists(env.fileSystem.getPath("/usr/adoble/templates/template.md")));
	    
	    // Now init with the template
	    String[] args = TestUtilities.argify("init -t /usr/adoble/templates/template.md");
	    int exitCode = ADR.run(args, env);
	    assertEquals(exitCode, 0);
	    
	    // Check that the template has been added to the properties. 
        ADRProperties properties = new ADRProperties(env);
		properties.load(); 
		String tFile = properties.getProperty("templateFile");
        assertNotNull(tFile);
		assertEquals(tFile, "/usr/adoble/templates/template.md" );
   

	    		
	    // Run the "new" command
	    args = TestUtilities.argify("new ADR created with template");
	    exitCode = ADR.run(args, env);
	    assertEquals(0, exitCode);

	    // Check if the ADR file has been created
	    Path newADRFile = fileSystem.getPath("/new_project/adr/doc/adr/0001-adr-created-with-template.md");
	    
	    TestUtilities.ls(env.fileSystem.getPath("/new_project/adr/doc/adr"));
	    assertTrue(Files.exists(fileSystem.getPath("/new_project/adr/doc/adr/0001-adr-created-with-template.md"))); 
	    	
		// Read in the file
	    Stream<String> lines= Files.lines(newADRFile);
		String actualContents = lines.collect(Collectors.joining("\n"));
		lines.close();
	    
	    // Compare
		assertEquals(expectedContents, actualContents);
	 		
	}
	
	/**
	 * Test templates with different file type, i.e. not always markdown.
	 * IN this we use ASCIIDOC with a file type of .txt
	 * @throws Exception
	 */
	//@Disabled("Need to write this test")
	@Test
	public void testDifferentFileTypes() throws Exception  {
		String templateContent = "= {{id}}. {{name}}\n" + 
				"Date: {{date}}\n" + 
				"== Status\n" + 
				"{{status}}\n" + 
				"== Links\n" + 
				"* {{{link.comment}}} {{{link.file}}}[ADR {{{link.id}}}]\n" + 
				"== Superseded\n" +
				"* Supersedes {{{superseded.file}}}[ADR {{{superseded.id}}}]\n" + 
				"== Content\n" + 
				"Some general content written in ASCIIDOC.";
		
		String expectedContents = "= 1. ADR created with ASCIIDOC template\n" + 
				"Date: {{date}}\n" + 
				"== Status\n" + 
				"Proposed\n" + 
				"== Links\n" + 
				"== Superseded\n" +
				"== Content\n" + 
				"Some general content written in ASCIIDOC.";
		expectedContents = expectedContents.replace("{{date}}", DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now()));
		
		// Create  a template
		TestUtilities.createTemplateFile(env.fileSystem, "/usr/adoble/templates/asciidoc_template.txt", templateContent);
	    assertTrue(Files.exists(env.fileSystem.getPath("/usr/adoble/templates/asciidoc_template.txt")));
	    
	    // Now init with the template
	    String[] args = TestUtilities.argify("init -t /usr/adoble/templates/asciidoc_template.txt");
	    int exitCode = ADR.run(args, env);
	    assertEquals(exitCode, 0);
	    
	    // Check that the template has been added to the properties. 
        ADRProperties properties = new ADRProperties(env);
		properties.load(); 
		String tFile = properties.getProperty("templateFile");
        assertNotNull(tFile);
		assertEquals(tFile, "/usr/adoble/templates/asciidoc_template.txt" );
   

	    		
	    // Run the "new" command
	    args = TestUtilities.argify("new ADR created with ASCIIDOC template");
	    exitCode = ADR.run(args, env);
	    assertEquals(0, exitCode);

	    // Check if the ADR file has been created
	    Path newADRFile = fileSystem.getPath("/new_project/adr/doc/adr/0001-adr-created-with-asciidoc-template.txt");
	    
	    TestUtilities.ls(env.fileSystem.getPath("/new_project/adr/doc/adr"));
	    assertTrue(Files.exists(fileSystem.getPath("/new_project/adr/doc/adr/0001-adr-created-with-asciidoc-template.txt"))); 
	    	
		// Read in the file
	    Stream<String> lines= Files.lines(newADRFile);
		String actualContents = lines.collect(Collectors.joining("\n"));
		lines.close();
	    
	    // Compare
		assertEquals(expectedContents, actualContents);
	}
}
