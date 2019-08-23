package org.doble.adr;

import org.junit.jupiter.api.*;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

public class CommandNewErrorTest {
	final static private String rootPathName = "/project/adr";
	final static private String docsPath = "/doc/adr";
	private static FileSystem fileSystem;
	private static Environment env;
	
	private static ByteArrayOutputStream errorBAOS;
	private static PrintStream errorPrintStream;
	
	@BeforeEach
	public void setUp() throws Exception {
		Path rootPath = null;

		// Set up the mock file system
		fileSystem = Jimfs.newFileSystem(Configuration.unix());

		rootPath = fileSystem.getPath("/project");

		Files.createDirectory(rootPath);
		
		// Redirect error message to a stream
		errorBAOS = new ByteArrayOutputStream();
		errorPrintStream = new PrintStream(errorBAOS);

		env = new Environment.Builder(fileSystem)
				.out(System.out)
				.err(errorPrintStream)
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
	
	@Test 
	public void testNoInit() {
		String adrTitle = "Trying to create an ADR without previous init";

		String[] args = TestUtilities.argify("new " + adrTitle);

		int exitCode = ADR.run(args, env);
		
		// Usage error
		//assertEquals(64, exitCode);   
		assertEquals(1, exitCode);   
		
		// Now check if a message has been given
		String commandErrorOutput = new String(errorBAOS.toByteArray());
        assertTrue(commandErrorOutput.contains("ERROR"));
	}
}
