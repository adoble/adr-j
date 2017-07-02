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
 			" * creates a subdirectory of the current wor,king directory" +
 			" * creates the first ADR in that subdirectory, recording the decision to" +
 			"   record architectural decisions with ADRs.\n\n" +
 			"If the DIRECTORY is not given, the ADRs are stored in the directory `doc/adr`."
		)   
public class CommandInit extends Command {

	
	private Properties properties;
	
	public CommandInit() {	
		// Set the default properties 
		properties = new Properties();
		properties.setProperty("docPath", "doc/adr");
	}
	

	/* (non-Javadoc)
	 * @see commands.Command#command(java.lang.String[])
	 */
	@Override
	public void command(String[] args) {
		FileOutputStream outProperties;
		
		boolean force = false; 
		
				
		switch (args.length) {
		case 1:
			if (args[0].equals("-f")) {
				force = true;
				System.out.println("Forcing initialisation. Current ADRS may be lost!");
			} else {
				properties.setProperty("docPath", args[0]);
			}
			break;
		case 2:
			if (args[0].equals("-f")) {
				force = true;
				System.out.println("Forcing initialisation. Current ADRS will be lost!");
			}
			properties.setProperty("docPath", args[1]);
			break;
		case 3:
			System.err.println("Unknown parameters");
			return;
		}			
	
		try {
			String rootPathName = System.getProperty("user.dir");
			Path adrPath = ADR.getFileSystem().getPath(".", ".adr");

			properties.setProperty("root", rootPathName);

			if (Files.notExists(adrPath) || force) {
				File dir = new File(".adr");
				dir.mkdir();
			} else {
				System.err.println("Already initialised");
				return;
			}

			
			// Create a properties file
			Path propPath = ADR.getFileSystem().getPath(rootPathName, ".adr/adr.properties");
			outProperties = new FileOutputStream(propPath.toString());
			properties.store(outProperties, null);
			outProperties.close();

			// Now create the docs directory which contains the adr directory
			Path docsPath = ADR.getFileSystem().getPath(properties.getProperty("root"),
					                                         properties.getProperty("docPath"));
			System.out.println("Creating ADR directory at " + docsPath);
			File docsDir = new File(docsPath.toString());
			docsDir.mkdirs();
			
			
			// Now generate template for the first architectural decision record and update the id
			Record record = new Record(); 
			
			record.id = 1;
			record.name = "Record architecture decisions";
			record.date = DateFormat.getDateInstance().format(new Date());
			
			record.store(docsPath); 
						
			outProperties = new FileOutputStream(propPath.toString());
			properties.store(outProperties, null);
			outProperties.close(); 

			

		} catch (ADRNotFoundException e) {
			System.err.println("FATAL: " + e.getMessage());
			System.exit(1); //TODO should this be at this level? 
		} catch (IOException e) {
			System.err.println("FATAL: initialise failed, reason: " + e.getMessage());
			System.exit(1); //TODO should this be at this level? 
		}

	}

}
