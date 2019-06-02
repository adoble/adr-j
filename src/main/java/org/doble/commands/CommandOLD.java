/**
 * 
 */
package org.doble.commands;

import org.doble.adr.*;
import org.doble.annotations.Cmd;

/**
 * @author adoble
 *
 */
public abstract class CommandOLD {
	protected Environment env;
	
	public CommandOLD(Environment env) throws ADRException{ 
		
		this.env = env; 
		
	}


	
	public abstract void command(String[] args) throws ADRException;
	
	
	/** 
	 * Get the usage help message for the command.
	 * This endures that, as commands are created, they are responsible for their own usage messages.
	 * The actual usage message for the command is specified in the Cmd annotation.
	 * @return The help message 
	 */
	public String getUsage() { 
		Cmd annotation = getAnnotation();
		
		return (annotation != null ? annotation.usage() : "" );
	}
	
	/**
	 * Get the short help message for the command. 
	 * 
	 * @return The short help message
	 */
	public String getShortHelp() {
		Cmd annotation = getAnnotation();
	
		return (annotation != null ? annotation.shorthelp() : "" );

	}
	
	/**
	 * Get the help message for the command. This ensures that, as commands are
	 * created, they are responsible for their own help messages.
	 * 
	 * @return The help message
	 */
	public String getHelp() {
		Cmd annotation = getAnnotation();
	
		return (annotation != null ? annotation.help() : "" );

	}
	
	/** Get the class Cmd annotation added to the Command sub-class
	 * @return Cmd the annotation on the Command sub-class
	 */
	private Cmd getAnnotation() {
		// 
		Cmd annotation = this.getClass().getAnnotation(Cmd.class);

		if (annotation != null && annotation instanceof Cmd) { 
			return annotation; 

		} else {
			return null;
		}
	}
	



}
