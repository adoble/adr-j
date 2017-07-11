package org.doble.adr;

import java.nio.file.Files;
import java.nio.file.Path;

/** Dummy class for testing that the editor runs
 * @author adoble
 *
 */
public class TestEditorRunner extends EditorRunner {

	public TestEditorRunner(Environment env) {
		super(env);
		
	}

	/** Just checks that the path exists. */ 
	@Override
	public void run(Path path) throws ADRException {
	   if (Files.notExists(path)) {
		   throw new ADRException("ADR path " + path.toString() + " does not exist");
	   }

	}

}
