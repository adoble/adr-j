package org.doble.adr;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CommandNewLinkTest {
	final static private String rootPathName = "/project/adr";
	final static private String docsPath = "/doc/adr";

	private static FileSystem fileSystem;

	private Environment env;
	private ADR adr;

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

		// Set up the directory structure
		adr = new ADR(env);

		String[] args = {"init"};
		adr.run(args);
	}

	@AfterEach
	public void tearDown() throws Exception {
		fileSystem.close();
	}

	@Test
	@Order(1)
	public void test1Links() throws Exception {
		// Create some ADRs
		adr.run(TestUtilities.argify("new An ADR"));
		adr.run(TestUtilities.argify("new Yet another adr"));
		adr.run(TestUtilities.argify("new This ADR is going to be linked to"));  // ADR id 4
		adr.run(TestUtilities.argify("new And even more decisions"));
		adr.run(TestUtilities.argify("new Decisions decisions decisions"));

		// Create new ADR that links to another
		adr.run(TestUtilities.argify("new -l \"4:Links to:Is linked to from\" Links to number 4"));

		// Now check if the link messages has been added to the new ADR 7)
		Path newADRFile = fileSystem.getPath(rootPathName, docsPath, "0007-links-to-number-4.md");

		assertTrue(TestUtilities.contains("Links to [ADR 4](0004-this-adr-is-going-to-be-linked-to.md)", newADRFile));

		// Now check that the link message has been added to the target ADR
		Path targetADRFile = fileSystem.getPath(rootPathName, docsPath, "0004-this-adr-is-going-to-be-linked-to.md");

		assertTrue(TestUtilities.contains("Is linked to from [ADR 7](0007-links-to-number-4.md)", targetADRFile));
	}

	@Order(2)
	public void test2MissingLInkSpec() {
		// Create new ADR that links to another
		assertThrows(ADRException.class, () -> {
			adr.run(TestUtilities.argify("new -l Links to number 4"));
		});
	}
}
