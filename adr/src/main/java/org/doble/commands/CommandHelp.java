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
	
	
	public CommandHelp(Environment env) {
		super(env);
	}

	/* (non-Javadoc)
	 * @see commands.Command#command(java.lang.String[])
	 */
	@Override
	public void command(String[] args) throws ADRException {
		Map<String, Class<?>> commandMap;
		Command command = new CommandNull(env); 

		// Build the map of the adr commands keyed with the command name.
		// All the commands are in the specified package.
		commandMap = ADR.buildCommandMap("org.doble.commands");  // FIXME use the reflections library and don't look for explicit packages
		
		// Now iterate through and print the usage 
		try {
			Collection<Class<?>> entries = commandMap.values();
			for (Class<?> entry : entries) {
				@SuppressWarnings("unchecked")
				Constructor<Command> ctor = (Constructor<Command>) entry.getConstructor(Environment.class);
				command = ctor.newInstance(env);

				// Get the usage
				env.out.println(command.getUsage());
				env.out.println("   " + command.getShortHelp());
			}
			
		} catch (Exception e) {
			throw new ADRException("FATAL: Cannot create the command class for producing help messages.");
		}
		

	}

}
