/**
 * 
 */
package org.doble.commands;

import org.doble.adr.ADRException;
import org.doble.adr.Environment;

/**
 * A command that does nothing.
 * @author adoble
 *
 */
public class CommandNull extends Command {
	private final String usage = "Usage: \n adr null";
    private final String help = "Only used internally .";
	public CommandNull(Environment env) throws ADRException { super(env);}
	
	
	/* (non-Javadoc)
	 * @see commands.Command#command(java.lang.String[])
	 */
	@Override
	public void command(String[] args) {
		// Do nothing

	}

	/* (non-Javadoc)
	 * @see commands.Command#getUsage()
	 */
	@Override
	public String getUsage() {
		return "Usage: " + "\n   " + usage; 
	}

	/* 
	 * @see commands.Command#getHelp()
	 */
	@Override
	public String getHelp() {
		return getUsage() + "\n\n" + help;
	}
	
	
}
