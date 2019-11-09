package org.doble.adr;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnvironmentTest {
	private FileSystem fileSystem;
	private final String ROOT_PATH = "/project/adr";
	private Path rootPath;
	private String editor = "C:/Users/adoble/AppData/Local/atom/bin/atom.cmd";
	private EditorRunner runner = new TestEditorRunner();
	private String author = "Andrew Doble";

	@BeforeEach
	public void setUp() throws Exception {
		fileSystem = Jimfs.newFileSystem(Configuration.unix());

		rootPath = fileSystem.getPath(ROOT_PATH);
		Files.createDirectories(rootPath);
	}

	@AfterEach
	public void tearDown() throws Exception {
		fileSystem.close();
	}

	@Test
	public void testEnvironment() {
		Environment env = new Environment.Builder(fileSystem)
				.out(System.out)
				.err(System.err)
				.in(System.in)
				.userDir(rootPath)
				.editorCommand(editor)
				.editorRunner(runner)
				.author(author)
				.build();

		assertEquals(env.fileSystem, fileSystem);
		assertEquals(env.out, System.out);
		assertEquals(env.err, System.err);
		assertEquals(env.in, System.in);

		Path p = env.dir;
		String pname = p.toString();
		assertEquals(pname, ROOT_PATH);

		assertEquals(env.editorCommand, editor);
		assertEquals(env.editorRunner, runner);

		assertEquals(env.author, author);
	}
}
