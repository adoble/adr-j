/**
 * 
 */
package org.doble.commands;

import java.lang.reflect.Constructor;
import java.util.*;


import org.doble.adr.*;
import org.doble.annotations.Cmd;

/**
 * @author adoble
 *
 */
@Cmd(name="help",
     usage="adr help",
     shorthelp="Help on the adr sub-commands.",
     help="Help ,on the adr sub-commands."
     )
public class CommandHelp extends Command {

	/* (non-Javadoc)
	 * @see commands.Command#command(java.lang.String[])
	 */
	@Override
	public void command(String[] args) {
		Map<String, Class<?>> commandMap;
		Command command = new CommandNull(); 

		// Build the map of the adr commands keyed with the command name.
		// All the commands are in the specified package.
		commandMap = ADR.buildCommandMap("org.doble.commands");  // FIXME use the reflections library and don't look for explicit packages
		
		// Now iterate through and print the usage 
		try {
			Collection<Class<?>> entries = commandMap.values();
			for (Class<?> entry : entries) {
				@SuppressWarnings("unchecked")
				Constructor<Command> ctor = (Constructor<Command>) entry.getConstructor();
				command = ctor.newInstance();

				// Get the usage
				System.out.println(command.getUsage());
				System.out.println("   " + command.getShortHelp());
			}
			
		} catch (Exception e) {
			System.out.println("FATAL: Cannot create the command class for producing help messages.");
			e.printStackTrace(System.err);
			System.exit(1);
		}
		

	}

}
