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
		
	public SystemEditorRunner(Environment env) {
		super(env);
	}

	/* (non-Javadoc)
	 * @see org.doble.adr.EditorRunner#run(java.nio.file.Path, org.doble.adr.Environment)
	 */
	@Override
	public void run(Path path) throws ADRException {
		
		String adrFileName = path.getFileName().toString();
		String adrPathName = path.toString();
				
		try {
			env.out.println("Opening Editor on " + adrFileName + " ...");
			Runtime runTime = Runtime.getRuntime();
			String cmd = env.editor + " " + adrPathName;
			runTime.exec(cmd);  
			
		} catch (IOException e) {
			throw new ADRException("FATAL: Could not open the editor.", e);
			
		}

	}

}
