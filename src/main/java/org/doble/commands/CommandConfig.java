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
		
		ADRProperties properties = loadProperties();
		
		Set<String> propertyKeys= properties.stringPropertyNames();
		String propStr = "";
		for (String key : propertyKeys) {
			propStr = key + "=" + properties.getProperty(key);
			env.out.println(propStr);
		}
		        
		return 0;
	}

	@Command(description = "Change the default name of the ADR author.") 
	void author(@Parameters(paramLabel = "<author>") String[] author) throws Exception {
		
		ADRProperties properties = loadProperties();
	
		String authorFullName = ""; 
		for (String	authorPart : author) 
		{ 
			authorFullName += authorPart + " "; 
		}
		authorFullName = authorFullName.trim(); 
		
		properties.setProperty("author", authorFullName);
		properties.store();
		
		
	}

	private ADRProperties loadProperties()  throws ADRException {
		env = commandADR.getEnvironment();
		
		properties = new ADRProperties(env);
					
		// Load the properties
		properties.load();
		
		return properties;
		
	}

	
	

}
