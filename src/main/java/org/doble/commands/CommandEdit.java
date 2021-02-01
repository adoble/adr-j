/**
 * @author adoble
 */

package org.doble.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import org.doble.adr.ADR;
import org.doble.adr.ADRFilter;
import org.doble.adr.ADRProperties;
import org.doble.adr.Environment;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;


@Command(name = "edit",
         description = "Starts the editor on the specified ADR"
         )
public class CommandEdit implements Callable<Integer> {
	@Parameters(paramLabel = "ADR_ID", description = "The identifier of the ADR to be edited.")
	private int adrId; 

	@ParentCommand
	private CommandADR commandADR;
	
	private Environment env;
	private ADRProperties properties;
	
	@Override
	public Integer call() throws Exception {
		int exitCode = CommandLine.ExitCode.OK; 

		env = commandADR.getEnvironment();

		// Load the properties
		properties = new ADRProperties(env);
		properties.load(); 


		// Determine where the .adr directory is stored, i.e. the root path. 
		// If the directory has not been initialised, this will throw an exception
		Path rootPath = ADR.getRootPath(env);

		// Get where the ADRs are stored
		Path docsPath = rootPath.resolve(properties.getProperty("docPath"));

		// Check to see if the editor command has been set.
		if (env.editorCommand == null) {
			String msg = "ERROR: Editor for the ADR has not been found in the environment variables.\n"
					+ "Have you set the environment variable ADR_EDITOR, ADR_VISUAL, EDITOR or VISUAL with the editor program you want to use?\n";
			env.err.println(msg);
			exitCode =  CommandLine.ExitCode.SOFTWARE;
		}
		// Find the specified ADR and launch the editor
		try {
			Path[] paths = Files.list(docsPath).filter(ADRFilter.filter(adrId)).toArray(Path[]::new);
			if (paths.length == 1) {
				// Start the editor
				env.editorRunner.run(paths[0], env.editorCommand);
			} else if (paths.length == 0) {
				String msg = "ERROR: The ADR with the identifier " + adrId + " not found!";
				env.err.println(msg);
				exitCode = CommandLine.ExitCode.USAGE;

			} else {
				String msg = "ERROR: More than one ADR with the identifier " + adrId + " found!";
				env.err.println(msg);
				exitCode = CommandLine.ExitCode.SOFTWARE;
			}
		} catch (IOException e) {
			String msg = "FATAL: unable to process request!";
			env.err.println(msg);
			exitCode = CommandLine.ExitCode.SOFTWARE;
		}

		return exitCode;
	}


	
}
