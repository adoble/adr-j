/**
 * 
 */
package org.doble.commands;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.doble.adr.*;
import org.doble.adr.template.Template;
import org.doble.adr.template.TemplateEngine;
import org.doble.annotations.Cmd;

/**
 * @author adoble
 *
 */
@Cmd(name="init", 
     usage="adr init [-d DIRECTORY] [-t ASCIIDOC | MARKDOWN]",
     shorthelp= "Initialises the directory of architecture decision records.",
     help= "Initialises the directory of architecture decision records:\n\n" +
 			" * creates a subdirectory of the current working directory" +
 			" * creates the first ADR in that subdirectory, recording the decision to" +
 			"   record architectural decisions with ADRs.\n\n" +
 			"If the DIRECTORY is not given, the ADRs are stored in the directory `doc/adr`." +
			 "If the template is not given, the ADRs use the MARKDOWN format."
		)   
public class CommandInit extends Command {

	private enum CommandStates {PARSE, DIRECTORY, TEMPLATE}

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
		


			properties.setProperty("docPath", ADRProperties.defaultDocPath); // Use the default value for the adr directory
			properties.setProperty("template", ADRProperties.defaultTemplate.name());
			CommandStates commandState = CommandStates.PARSE;

			for (String arg : args) {

				switch (commandState) {
					case PARSE:
						if (arg.equals("-d")) {
							commandState = CommandStates.DIRECTORY;
						} else if (arg.equals("-t")) {
							commandState = CommandStates.TEMPLATE;
						}
						break;
					case DIRECTORY:
						properties.setProperty("docPath", arg);
						commandState = CommandStates.PARSE;
						break;
					case TEMPLATE:
						properties.setProperty("template", arg);
						commandState = CommandStates.PARSE;
						break;
				}
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
			TemplateEngine templateEngine = Template.valueOf(properties.getProperty("template")).templateEngine();

			env.out.println("Creating ADR directory at " + docsPath);
			Files.createDirectories(docsPath);


			// Now generate template for the first architectural decision record and update the id
			Record record = new Record.Builder(docsPath, templateEngine)
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
