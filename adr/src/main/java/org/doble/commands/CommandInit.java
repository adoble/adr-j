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
	
	
	
	public CommandInit(Environment env) {	
		super(env);
		properties = new Properties();
		properties.setProperty("docPath", "doc/adr");
	}
	

	/* (non-Javadoc)
	 * @see commands.Command#command(java.lang.String[])
	 */
	@Override
	public void command(String[] args) throws ADRException {
		FileOutputStream outProperties;
		
		boolean force = false; 
		
				
		switch (args.length) {
		case 1:
			if (args[0].equals("-f")) {
				force = true;
				env.out.println("Forcing initialisation. Current ADRS may be lost!");
			} else {
				properties.setProperty("docPath", args[0]);
			}
			break;
		case 2:
			if (args[0].equals("-f")) {
				force = true;
				env.out.println("Forcing initialisation. Current ADRS will be lost!");
			}
			properties.setProperty("docPath", args[1]);
			break;
		case 3:
			throw new ADRException("Unknown parameters");
		}			
	
		try {
			//String rootPathName = env.dir.toString();
						
			//Path adrPath = env.fileSystem.getPath(".", ".adr");
			
			Path adrPath = env.dir.resolve(".adr");

			//properties.setProperty("root", rootPathName);
			properties.setProperty("root", adrPath.toString());

			if (Files.notExists(adrPath) || force) {
				Files.createDirectories(adrPath);
//				File dir = new File(".adr");
//				dir.mkdir();
			} else {
				throw new ADRException("Directory is already initialised for ADR.");
			}

			
			// Create a properties file
			//Path propPath = env.fileSystem.getPath(rootPathName, ".adr/adr.properties");
			Path propPath = adrPath.resolve("adr.properties");
			Files.createFile(propPath);
			
		    BufferedWriter writer =  Files.newBufferedWriter(propPath);
			
			properties.store(writer, null);
			writer.close();

			// Now create the docs directory which contains the adr directory
			//Path docsPath = env.fileSystem.getPath(properties.getProperty("root"),  properties.getProperty("docPath"));
			Path docsPath = env.dir.resolve(properties.getProperty("docPath"));
			
			env.out.println("Creating ADR directory at " + docsPath);
			Files.createDirectories(docsPath);
				
			
			// Now generate template for the first architectural decision record and update the id
			Record record = new Record(); 
			
			record.id = 1;
			record.name = "Record architecture decisions";
			record.date = DateFormat.getDateInstance().format(new Date());
			
			record.store(docsPath); 
						
			/*outProperties = new FileOutputStream(propPath.toString());
			properties.store(outProperties, null);
			outProperties.close(); */

			

		} catch (ADRNotFoundException e) {   //TODO Check why we need an extra ADR exception type
			throw new ADRException("FATAL: " + e.getMessage());
		} catch (IOException e) {
			throw new ADRException("FATAL: initialise failed, reason: " + e.getMessage());
		}

	}

}
