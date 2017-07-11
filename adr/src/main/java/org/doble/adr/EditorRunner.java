package org.doble.adr;

import java.nio.file.Path;


/** Abstract class  to start up an editor on the file specified. */
public abstract class EditorRunner {
	@SuppressWarnings("unused")
	final protected Environment env;
	
	public EditorRunner (Environment env) {
		this.env = env;
	}
	
	 
    abstract  public void run(Path path) throws ADRException ;
}
