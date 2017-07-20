package org.doble.adr;

import java.nio.file.Path;


/** Abstract class  to start up an editor on the file specified. */
public abstract class EditorRunner {
	
	 
    abstract  public void run(Path path, String editorCommand) throws ADRException ;
}
