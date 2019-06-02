/**
 * 
 */
package org.doble.commands;


import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import org.doble.adr.*;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

/**
 * Subcommand to list the filenames of the currently created architecture decision records
 * 
 * @author adoble
 *
 */

@Command(name = "list",
         description = "Lists the filenames of the currently created architecture decision records.")
public class CommandList implements Callable<Integer> {
	@ParentCommand
	CommandADR commandADR;
	
	private Environment env;
	private ADRProperties properties; 

	/**
	 * 
	 */
	public CommandList()  {

	}

	/* (non-Javadoc)
	 * @see commands.Command#command(java.lang.String[])
	 */
	@Override
	public Integer call() {
		
		env = commandADR.getEnvironment();
		
		properties = new ADRProperties(env);
					
		// Load the properties
		try {
			properties.load();
		} catch (ADRException e) {
			env.err.println("FATAL: Cannot load properties file. Exception message ->" + e.getMessage() );
			return ADR.ERRORGENERAL;
		}
		
		Path rootPath;
		try {
			rootPath = ADR.getRootPath(env);
		} catch (ADRException e) {
			env.err.println("FATAL: Cannot determine project root directory. Exception message ->" + e.getMessage() );
			return ADR.ERRORGENERAL;
		}
		
		
		Path docsPath = rootPath.resolve(properties.getProperty("docPath"));

		try (Stream<Path> stream = Files.list(docsPath)){
			stream.map(Path::getFileName).filter(ADRFilter.filter()).forEachOrdered(env.out::println);
		} catch (IOException e) {
			env.out.println("FATAL: Cannot access directory. Exception message ->" + e.getMessage());
			return ADR.ERRORGENERAL;
		}

		        
		return 0;
	}



}
