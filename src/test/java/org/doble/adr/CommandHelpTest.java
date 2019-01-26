/**
 *
 */
package org.doble.adr;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.FileSystem;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

// Import a mock of the file systes

/**
 * @author adoble
 */
public class CommandHelpTest {
	private static FileSystem fileSystem;
	private final String rootPath = "/project/adr";

	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
		// Set up the mock file system
		fileSystem = Jimfs.newFileSystem(Configuration.unix());
	}

	@Test
	public void testHelp() throws Exception {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);

		Environment env = new Environment.Builder(fileSystem)
				.out(ps)
				.err(System.err)
				.in(System.in)
				.userDir(rootPath)
				.build();
		ADR adr = new ADR(env);

		String[] args = {"help"};

		adr.run(args);

		// read the output
		String content = new String(baos.toByteArray());

		assertTrue(content.length() > 0);

		assertTrue(content.contains("Help"));  //At least this command is shown
	}

	@Test
	public void testNoArgs() {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);

		Environment env = new Environment.Builder(fileSystem)
				.out(ps)
				.err(System.err)
				.in(System.in)
				.userDir(rootPath)
				.build();
		ADR adr = new ADR(env);

		String[] args = {};

		try {
			adr.run(args);
		} catch (ADRException e) {
			fail("ADR Exception raised: " + e.getMessage());
		}

		// read the output
		String content = new String(baos.toByteArray());

		assertTrue(content.length() > 0);

		assertTrue(content.contains("init"));  //At least this command is shown
		assertTrue(content.contains("help"));  //At least this command is shown
		assertTrue(content.contains("new"));   //At least this command is shown
	}
}
