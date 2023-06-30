package org.doble.commands;

import java.util.concurrent.Callable;

import org.doble.adr.Environment;

import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;

/** 
 * This class represents the main command "adr". It is:
 *  - required for the Picocli framework
 *  - contains the command declarations 
 *  - maintains the environment for subcommands
 *  
 * @author adoble
 *
 */

@Command(name = "adr",
		 description = "Creation and management of architectural decision records (ADRs)",
		 version = "3.2.1",
		 exitCodeListHeading = "Exit Codes:%n",
		 exitCodeList        = { " 0:Successful program execution.",
				 				 "64:Invalid input: an unknown option or invalid parameter was specified.",
				                 "70:Execution exception: an exception occurred while executing the business logic."
		 },
		 subcommands = {CommandInit.class, 
		         		CommandNew.class,
				        CommandList.class,
				        CommandVersion.class,
				        CommandEdit.class,
				        CommandConfig.class, 
				        CommandLink.class,
				        HelpCommand.class   // Built in help command
				       }
		)
public class CommandADR implements Callable<Integer> {
	
	Environment env;
	
     
	public CommandADR(Environment env) {
		super();
		this.env = env;
	}
	
	public Environment getEnvironment() {
		return env;
	}


	@Override
	public Integer call() throws Exception {
		return 0;
	}

}
