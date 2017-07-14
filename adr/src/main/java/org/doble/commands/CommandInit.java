/**
 * 
 */
package org.doble.commands;

import java.io.*;
import java.nio.file.*;
import java.text.DateFormat;
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
	final private String defaultADRDirectory = "docs/adr";



	public CommandInit(Environment env) throws ADRException {	
		super(env);
		properties = new Properties();
		//properties.setProperty("docPath", "doc/adr");
	}


	/* (non-Javadoc)
	 * @see commands.Command#command(java.lang.String[])
	 */
	@Override
	public void command(String[] args) throws ADRException {

		switch (args.length) {
		case 0:
			properties.setProperty("docPath", defaultADRDirectory); // Use the default value for the adr directory
			break;

		case 1:
			properties.setProperty("docPath", args[0]);
			break;

		default:
			throw new ADRException("ERROR: Unknown parameters");
		}		


		try {
			Path adrPath = env.dir.resolve(".adr");


			properties.setProperty("root", adrPath.toString());

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
			Record record = new Record(docsPath); 

			record.id = 1;
			record.name = "Record architecture decisions";
			record.date = DateFormat.getDateInstance().format(new Date());

			record.store(); 

			/*outProperties = new FileOutputStream(propPath.toString());
			properties.store(outProperties, null);
			outProperties.close(); */
		}
		catch (Exception e) {
			throw new ADRException("FATAL: Unable to initialise.", e);
		}


	}

}
