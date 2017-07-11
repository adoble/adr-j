package org.doble.adr;

import static org.junit.Assert.*;

import java.nio.file.*;


import org.junit.*;


import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

public class EnvironmentTest {
	private FileSystem fileSystem;
	private final String ROOT_PATH = "/project/adr";
	private Path rootPath; 
	private String editor = "C:/Users/adoble/AppData/Local/atom/bin/atom.cmd";
	private EditorRunner runner = new TestEditorRunner(null);

	@Before
	public void setUp() throws Exception {
		try {
			fileSystem = Jimfs.newFileSystem(Configuration.unix());
			
			rootPath = fileSystem.getPath(ROOT_PATH);
			Files.createDirectories(rootPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Test
	public void testEnvironment() {
		Environment env = new Environment.Builder(fileSystem)
				.out(System.out)
				.err(System.err)
				.in(System.in)
				.userDir(rootPath)
				.editor(editor)
				.editorRunner(runner)
				.build();
		

	assertTrue(env.fileSystem.equals(fileSystem));
	assertTrue(env.out.equals(System.out));
	assertTrue(env.err.equals(System.err));
	assertTrue(env.in.equals(System.in));
	
	Path p = env.dir;
	String pname = p.toString();
	assertTrue(pname.equals(ROOT_PATH));
	
	assertTrue(env.editor.equals(editor));
	assertTrue(env.editorRunner.equals(runner));
	}

}
