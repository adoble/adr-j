package org.doble.commands;

import java.nio.file.*;
import java.util.Set;
import java.util.concurrent.Callable;

import org.doble.adr.*;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

/**
 * Subcommand to configure the properties
* 
 * @author adoble
 *
 */

@Command(name = "config",
description = "List of the currently set properties.")
public class CommandConfig  implements Callable<Integer> {
	@ParentCommand
	CommandADR commandADR;
	
	private Environment env;
	private ADRProperties properties; 
	
	/**
	 * 
	 */
	public CommandConfig()  {

	}
	
	/* The commmand 
	 *   <code>adr config </code>
	 * lists the currently set properties in the properties file. 
	 * 
	 * TODO Extend this with subcommands for each of the properties 
	 * that can be set.
	 * 
	 * @see commands.Command#command(java.lang.String[])
	 */
	@Override
	public Integer call() throws Exception {
		
		env = commandADR.getEnvironment();
		
		properties = new ADRProperties(env);
					
		// Load the properties
		try {
			properties.load();
		} catch (ADRException e) {
			env.err.println("FATAL: Cannot load properties file. Exception message ->" + e.getMessage() );
			return ADR.ERRORGENERAL;
		}
		
		Set<String> propertyKeys= properties.stringPropertyNames();
		String propStr = "";
		for (String key : propertyKeys) {
			propStr = key + "=" + properties.getProperty(key);
			env.out.println(propStr);
		}
		        
		return 0;
	}
	
	//TODO 

	@Command(description = "Change the default name of the ADR author.") 
	void author(@Parameters(paramLabel = "<author>") String[] author) {

		String authorFullName = ""; 
		for (String	authorPart : author) 
		{ 
			authorFullName += authorPart + " "; 
		}
		authorFullName.trim(); 
		System.out.println(authorFullName);
		
	}

	

}
