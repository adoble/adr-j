/**
 * 
 */
package org.doble.commands;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.doble.adr.*;
import org.doble.annotations.Cmd;

/**
 * @author adoble
 *
 */
@Cmd(name="init", 
     usage="adr init [DIRECTORY]", 
     shorthelp= "Initialises the directory of architecture decision records.",
     help= "Initialises the directory of architecture decision records:\n\n" +
 			" * creates a subdirectory of the current working directory" +
 			" * creates the first ADR in that subdirectory, recording the decision to" +
 			"   record architectural decisions with ADRs.\n\n" +
 			"If the DIRECTORY is not given, the ADRs are stored in the directory `doc/adr`."
		)   
public class CommandInit extends Command {

	private Properties properties;
	
	public CommandInit(Environment env) throws ADRException {	
		super(env);
		properties = new Properties();
	}


	/* (non-Javadoc)
	 * @see commands.Command#command(java.lang.String[])
	 */
	@Override
	public void command(String[] args) throws ADRException {
		

		if (env.editorCommand == null) {
			String msg = "WARNING: Editor for the ADR has not been found in the environment variables.\n"
				    	+ "Have you set the environment variable EDITOR or VISUAL with the editor program you want to use?\n";
			env.err.println(msg);
		} 
		

		switch (args.length) {
		case 0:
			properties.setProperty("docPath", ADRProperties.defaultDocPath); // Use the default value for the adr directory
			break;

		case 1:
			properties.setProperty("docPath", args[0]);
			break;

		default:
			throw new ADRException("ERROR: Unknown parameters");
		}		


		try {
			Path adrPath = env.dir.resolve(".adr");


			if (Files.notExists(adrPath)) {
				Files.createDirectories(adrPath);
			} else {
				throw new ADRException("Directory is already initialised for ADR.");
			}


			// Create a properties file
			Path propPath = adrPath.resolve("adr.properties");
			Files.createFile(propPath);

			BufferedWriter writer =  Files.newBufferedWriter(propPath);

			properties.store(writer, null);
			writer.close();

			// Now create the docs directory which contains the adr directory
			Path docsPath = env.dir.resolve(properties.getProperty("docPath"));

			env.out.println("Creating ADR directory at " + docsPath);
			Files.createDirectories(docsPath);


			// Now generate template for the first architectural decision record and update the id
			Record record = new Record.Builder(docsPath)
					                    .id(1)
					                    .name("Record architecture decisions")
					                    .date(new Date())
					                    .build(); 
				
			record.store(); 

		}
		catch (Exception e) {
			throw new ADRException("FATAL: Unable to initialise.", e);
		}


	}

}
