/**
 * 
 */
package org.doble.commands;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.Callable;

import org.doble.adr.*;
//import org.doble.annotations.Cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

/**
 * @author adoble
 *
 */
@Command(name = "init",
         description = "Initialises the directory of architecture decision records:\n" +
      			       " * creates a subdirectory of the current working directory\n" +
     			       " * creates the first ADR in that subdirectory, recording the decision to\n" +
     			       "   record architectural decisions with ADRs."
    	)
public class CommandInit implements Callable<Integer> {
	
	@Option(names = { "-t", "--template" }, paramLabel = "TEMPLATE", description = "Template file used for ADRs.")
    private String template;

    @Parameters(arity = "0..1", paramLabel = "DOCDIR", description = "The directory to store the ADRs relative to " 
    		                                                         + " the current directory."
    		                                                         + " Default is ${DEFAULT-VALUE}.")
    private String docPath = ADRProperties.defaultDocPath;  //TODO change this to type path
    
    @ParentCommand
    private CommandADR commandADR;
	
	private Environment env;
	private Properties properties;
	

	/* (non-Javadoc)
	 * @see commands.Command#command(java.lang.String[])
	 */
	@Override
	public Integer call() throws Exception {
		int exitCode = 0; 

		this.env = commandADR.getEnvironment();
		properties = new Properties();
		
		if (env.editorCommand == null) {
			String msg = "WARNING: Editor for the ADR has not been found in the environment variables.\n"
					+ "Have you set the environment variable EDITOR or VISUAL with the editor program you want to use?\n";
			env.err.println(msg);
			exitCode = ADR.ERRORENVIRONMENT;
		} 

		properties.setProperty("docPath", docPath); // Use the default value for the adr directory


		Path adrPath = env.dir.resolve(".adr");


		if (Files.notExists(adrPath)) {
			Files.createDirectories(adrPath);
		} else {
			env.out.println("Directory is already initialised for ADR.");
			return ADR.ERRORGENERAL;
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

		return exitCode;
	}

}
