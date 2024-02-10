/**
 * Prints the version of adr-j
 */
package org.doble.commands;

import java.util.concurrent.Callable;

import org.doble.adr.Environment;
import org.doble.adr.Version;

import picocli.CommandLine.*;

/**
 * Subcommand print out the version number.
 * 
 * @author adoble
 */

@Command(name = "version", description = "Prints the version of adr-j.")
public class CommandVersion implements Callable<Integer> {

	/*******************************************************************************************
	 * VERSION NUMBER *
	 * *
	 * Version numbers adhere to to Semantic Versioning:
	 * https://semver.org/spec/v2.0.0.html *
	 * *
	 *******************************************************************************************/
	// private String version = "3.2.3-alpha"; // Minor release, backwards
	// compatible

	@ParentCommand
	CommandADR commandADR;

	private Environment env;

	@Override
	public Integer call() throws Exception {
		int exitCode = 0;

		env = commandADR.getEnvironment();
		String msg = "Version " + Version.get_version();
		env.err.println(msg);

		return exitCode;
	}

}
