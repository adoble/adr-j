/**
 * 
 */
package org.doble.commands;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


import org.doble.adr.*;
import org.doble.annotations.Cmd;

/**
 * @author adoble
 *
 */
@Cmd(name="help",
     usage="adr help <command>",
     shorthelp="Help on the adr sub-commands.",
     help="Help on the adr sub-commands. \n\n" +
			 "Specific, more detailed, help on a command can be found by using the\n" +
			 "<command> parameter, e.g. adr help init. "
     )
public class CommandHelp extends Command {

	// Map of the available adr commands
	Map<String, Class<?>> commandMap;
	
	public CommandHelp(Environment env) throws ADRException{
		super(env);

		// Build the map of the adr commands keyed with the command name.
		// All the commands are in the specified package.
		commandMap = ADR.buildCommandMap("org.doble.commands");  // TODO  use the reflections library and don't look for explicit packages

	}

	/* (non-Javadoc)
	 * @see commands.Command#command(java.lang.String[])
	 */
	@Override
	public void command(String[] args) throws ADRException {

		// Now iterate through and print the usage
		if (args.length == 0) {
			printSummary();
		} else {
			//Extract the name of the command on which help is required
			// and print the help on that command.
			// If more arguments are available then ignore them (we are,
			// after all, trying to help the user).
			printHelp(args[0]);
		}


	}

	/**
	 * Print a summary of the usage for each command
	 */
	private void printSummary() throws ADRException {
		Command command;
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

	/**
	 * Print out the help for a command
	 */
	private void printHelp(String commandName) throws ADRException {
		Command command;

		try {
			// Find the name in the command map
			Class<?> entry = commandMap.get(commandName);
			@SuppressWarnings("unchecked")
			Constructor<Command> ctor = (Constructor<Command>) entry.getConstructor(Environment.class);
			command = ctor.newInstance(env);

			// Get the help
			env.out.println(command.getUsage());
			env.out.println("   " + command.getHelp());
		} catch (Exception e) {
			throw new ADRException("FATAL: Cannot create the command class for producing help messages.");
		}
	}


}
