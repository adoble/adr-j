/**
 *
 */
package org.doble.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.OptionalInt;

import org.doble.adr.ADR;
import org.doble.adr.ADRException;
import org.doble.adr.ADRProperties;
import org.doble.adr.EditorRunner;
import org.doble.adr.Environment;
import org.doble.adr.LinkSpecificationException;
import org.doble.adr.Record;
import org.doble.annotations.Cmd;

/**
 * @author adoble
 */
@Cmd(name = "new",
		usage = "adr new [-s <superseded_adr>] [-l <target_adr>:<link_description>:<reverse_link_description>] TITLE_TEXT...",
		shorthelp = "Creates a new, numbered ADR.",
		help = " Creates a new, numbered ADR.  The TITLE_TEXT arguments are concatenated to" +
				" form the title of the new ADR.  The ADR is opened for editing in the" +
				" editor specified by the VISUAL or EDITOR environment variable (VISUAL is" +
				" preferred; EDITOR is used if VISUAL is not set).  After editing, the" +
				" file name of the ADR is output to stdout, so the command can be used in" +
				" scripts.\n\n" +
				" Options:\n\n" +
				" -s <superseded_adr>   A reference (number or partial filename) of a previous" +
				"                 decision that the new decision supersedes. A Markdown link" +
				"                 to the superseded ADR is inserted into the Status section." +
				"                 The status of the superseded ADR is changed to record that" +
				"                 it has been superseded by the new ADR.\n\n" +
				" -l <target_adr>:<link_description>:<reverse_link_description>" +
				"                 Links the new ADR to a previous ADR." +
				"                 <target_adr> is a reference (number or partial filename) of a" +
				"                 previous decision. " +
				"                 <link_description> is the description of the link created in the new ADR." +
				"                 <reverse_link_description> is the description of the link created in the" +
				"                 existing ADR that will refer to the new ADR.\n\n" +
				" Multiple -s and -l options can be given, so that the new ADR can supersede" +
				" or link to multiple existing ADRs."
)
public class CommandNew extends Command {

	private enum CommandStates {PARSE, SUPERSEDES, LINK, RECORD}

	;
	ADRProperties properties;

	/**
	 *
	 */
	public CommandNew(Environment env) throws ADRException {
		super(env);

		properties = new ADRProperties(env);

		// Load the properties
		properties.load();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see commands.Command#command(java.lang.String[])
	 */
	@Override
	public void command(String[] args) throws ADRException {

		// This stores TITLE_TEXT
		String name = "";

		String link = "";

		ArrayList<Integer> supersedes = new ArrayList<Integer>();

		// Determine where the ADRs are stored and 
		// set up the record object 
		Path rootPath = ADR.getRootPath(env);
		Path docsPath = rootPath.resolve(properties.getProperty("docPath"));

		// Check to see if the editor command has been set.
		if (env.editorCommand == null) {
			String msg = "ERROR: Editor for the ADR has not been found in the environment variables.\n"
					+ "Have you set the environment variable EDITOR or VISUAL with the editor program you want to use?\n";
			throw new ADRException(msg);
		}

		try {
			if (args.length == 0) {
				String msg = "ERROR: Try giving the ADR a description.\n";
				msg += this.getUsage();
				throw new ADRException(msg);
			} else {
				CommandStates commandState = CommandStates.PARSE;
				for (String arg : args) {

					switch (commandState) {
						case PARSE:
							if (arg.equals("-s")) {
								commandState = CommandStates.SUPERSEDES;
							} else if (arg.equals("-l")) {
								commandState = CommandStates.LINK;
							} else {
								commandState = CommandStates.RECORD;
								name += arg + " ";
							}
							break;
						case SUPERSEDES:
							supersedes.add(Integer.parseInt(arg));
							//record.addSupersedes(Integer.parseInt(arg));
							commandState = CommandStates.PARSE;
							break;
						case LINK:
							link = arg;
							commandState = CommandStates.PARSE;
							break;
						case RECORD:
							name += arg + " ";
							break;
					}
				}
				if (commandState == CommandStates.SUPERSEDES) {
					String msg = "ERROR: -s option requires an ADR reference.\n";
					msg += "Usage: " + getUsage();
					throw new ADRException(msg);
				}
				if (commandState == CommandStates.LINK) {
					String msg = "ERROR: -l option requires an ADR reference.\n";
					msg += "Usage: " + getUsage();
					throw new ADRException(msg);
				}
			}
		} catch (NumberFormatException e) {
			String msg = "ERROR: Invalid ADR reference for the option -s \n";
			msg += this.getUsage();
			throw new ADRException(msg);
		}

		Record record = new Record.Builder(docsPath)
				.id(highestIndex() + 1)
				.name(name)
				.date(new Date())
				.build();

		for (Integer supersedeId : supersedes) {
			record.addSupersedes(supersedeId);
		}

		try {
			record.addLink(link);
		} catch (LinkSpecificationException e) {
			String msg = "ERROR: -l parameter incorrectly formed.";
			msg += "Usage: " + getUsage();
			throw new ADRException(msg);
		}

		createADR(record);
	}

	private void createADR(Record record) throws ADRException {
		// The ADR file that is created
		Path adrPath;

		adrPath = record.store();

		// And now start up the editor using the specified runner
		EditorRunner runner = env.editorRunner;

		env.out.println("Opening Editor on " + adrPath.toString() + " ...");

		runner.run(adrPath, env.editorCommand);

		env.out.println(adrPath.toString());  // Return the file name of the ADR
	}

	/**
	 * Find the highest index of the ADRs in the adr directory by iterating
	 * through all the files
	 *
	 * @return int The highest index found. If no files are found returns 0.
	 */
	private int highestIndex() throws ADRException {
		OptionalInt highestIndex;

		Path docPath = env.fileSystem.getPath(properties.getProperty("docPath"));
		Path rootPath = ADR.getRootPath(env);
		Path adrPath = rootPath.resolve(docPath);

		try {
			highestIndex = Files.list(adrPath).mapToInt(CommandNew::toInt).max();
		} catch (IOException e) {
			throw new ADRException("FATAL: Unable to determine the indexes of the ADRs.", e);
		}

		return (highestIndex.isPresent() ? highestIndex.getAsInt() : 0);
	}

	private static int toInt(Path p) {
		String name = p.getFileName().toString();

		// Extract the first 4 characters
		String id = name.substring(0, 4);
		return new Integer(id);
	}
}
