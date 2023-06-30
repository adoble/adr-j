/**
 *
 */
package org.doble.commands;


import java.util.concurrent.Callable;

import org.doble.adr.Environment;

import picocli.CommandLine.*;


/**
 * Subcommand print out the version number.
 * 
 * @author adoble
 */

@Command(name = "version",
         description = "Prints the version of adr-j.")
public class CommandVersion implements Callable<Integer> {
	
	
	/*******************************************************************************************
	 *                                  VERSION NUMBER                                         *
	 *                                                                                         *
	 * Version numbers adhere to to Semantic Versioning:  https://semver.org/spec/v2.0.0.html  *
	 *                                                                                         *
	 *******************************************************************************************/
	private String version = "3.2.1";  // Minor release, backwards compatible 
		
	
	@ParentCommand
	CommandADR commandADR; 
	
	private Environment env;

	@Override
	public Integer call()  throws Exception {
		int exitCode = 0; 
		
		env = commandADR.getEnvironment();
		String msg = "Version " + version;
		env.err.println(msg);
		
		return exitCode;
	}

}
