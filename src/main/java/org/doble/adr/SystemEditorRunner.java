/**
 * Fires up the editor when the tool is being invoked from the command line.
 *
 */
package org.doble.adr;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author adoble
 *
 */
public class SystemEditorRunner extends  EditorRunner {

	/* (non-Javadoc)
	 * @see org.doble.adr.EditorRunner#run(java.nio.file.Path, org.doble.adr.Environment)
	 */
	@Override
	public void run(Path path, String editorCommand) throws ADRException {

		String adrPathName = path.toString();
		Process p = null;
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(editorCommand,adrPathName);
			processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
			processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
			processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);

			p = processBuilder.start();
			p.waitFor();

		} catch (IOException | InterruptedException e) {
			throw new ADRException("FATAL: Could not open the editor.", e);
		} finally {
			if (p != null){
				p.destroy();
			}
		}

	}

}
