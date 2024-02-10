package org.doble.commands;

import java.util.*;
import java.util.concurrent.Callable;

import org.doble.adr.*;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

/**
 * Subcommand to link ADRs with one another
 * 
 * TODO expand this description
 * 
 * @author adoble
 *
 */

@Command(name = "link", description = "Links ADRs with one another.")
public class CommandLink implements Callable<Integer> {
	@Parameters(hidden = true)
	List<String> parameters;
	@Parameters(index = "0", description = "Identifier of ADR to be linked from")
	int sourceADRId;
	@Parameters(index = "1", description = "Identifier of ADR to be linked to")
	int targetADRId;

	@Option(names = "-sd", arity = "0..1", description = "Description of the source link")
	String sourceDescription;

	@Option(names = "-td", arity = "0..1", description = "Description of the target link")
	String targetDescription;

	@ParentCommand
	private CommandADR commandADR;

	private Environment env;
	private ADRProperties properties;

	/**
	 * TODO
	 */
	public CommandLink() {

	}

	@Override
	public Integer call() throws ADRException {
		env = commandADR.getEnvironment();

		properties = new ADRProperties(env);

		// Load the properties
		try {
			properties.load();
		} catch (ADRException e) {
			env.err.println("FATAL: Cannot load properties file. Exception message ->" + e.getMessage());
			return ADR.ERRORGENERAL;
		}

		env.out.println("Source ADR ID:" + sourceADRId);
		env.out.println("Target ADR ID:" + targetADRId);
		env.out.println("Source ADR Description:" + sourceDescription);
		env.out.println("Target ADR Description:" + targetDescription);

		return 0;
	}
}
