/**
 *
 */
package org.doble.commands;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.doble.adr.ADR;
import org.doble.adr.ADRException;
import org.doble.adr.ADRProperties;
import org.doble.adr.EditorRunner;
import org.doble.adr.Environment;
import org.doble.adr.LinkSpecificationException;
import org.doble.adr.Record;
//import org.doble.annotations.Cmd;

import picocli.CommandLine;
import picocli.CommandLine.*;


/**
 * Subcommand the create a new, numbered Architecture Description Record (ADR).
 * 
 * @author adoble
 */

@Command(name = "new",
         description = "Creates a new, numbered ADR.  The <title_text> arguments are concatenated to"
         	      	+ " form the title of the new ADR. The ADR is opened for editing in the" 
         	      	+ " editor specified by the VISUAL or EDITOR environment variable (VISUAL is " 
         	      	+ "preferred; EDITOR is used if VISUAL is not set).  After editing, the " 
         	      	+ "file name of the ADR is output to stdout, so the command can be used in " 
         	      	+ "scripts.")
public class CommandNew implements Callable<Integer> {
	// This stores the arguments making up the title text
	@Parameters(arity = "1..*", paramLabel = "TITLETEXT", 
			    description = "The TITLETEXT arguments are concatenated to form the title of the new ADR.")
	List<String> adrTitleParts;
	
    @Option(names = {"-l", "-link"}, description = "Links the new ADR to a previous ADR. "
    		+ " A specification of a link to another ADR is in the form \n"
    		+ "   <target_adr>:<link_description>:<reverse_link_description>\n\n"
    		+ " <target_adr> is a reference (number or partial filename) of a previous decision."
    		+ " <link_description> is the description of the link created in the new ADR."
    		+ "Multiple -l options can be given, so that the new ADR can link to multiple existing ADRs"
    		)
	ArrayList<String> links = new ArrayList<String>();;

	@Option(names = {"-s", "supersedes"}, description = "A reference (number) of a previous"
			+ " decision that the new decision supersedes. A markdown"
			+ " to the superseded ADR is inserted into the Status section."
			+ "	The status of the superseded ADR is changed to record that"
			+ " it has been superseded by the new ADR."
			+ " Multiple -s options can be given, so that the new ADR can supersede multiple existing ADRs")
	ArrayList<Integer> supersedes = new ArrayList<Integer>();
	
	@ParentCommand
	CommandADR commandADR; 
		
	private Environment env;
	
	ADRProperties properties;



	@Override
	public Integer call()  throws Exception {
		String adrTitle;
		int exitCode = 0; 
		
		env = commandADR.getEnvironment();

		properties = new ADRProperties(env);
		
		// Load the properties
		properties.load(); 


		// Determine where the ADRs are stored and 
		// set up the record object 
		Path rootPath = ADR.getRootPath(env);
		Path docsPath = rootPath.resolve(properties.getProperty("docPath"));

		// Check to see if the editor command has been set.
		if (env.editorCommand == null) {
			String msg = "ERROR: Editor for the ADR has not been found in the environment variables.\n"
					+ "Have you set the environment variable EDITOR or VISUAL with the editor program you want to use?\n";
			env.err.println(msg);
			exitCode =  ADR.ERRORENVIRONMENT;
		}
		
		// Set up the template file
		Path templatePath = null;
		String templatePathName = properties.getProperty("templateFile");

	    System.out.println("templatePathName------->"+templatePathName);
	    if (templatePathName != null) {
		    templatePath = env.fileSystem.getPath(templatePathName);
		    if (!Files.exists(templatePath)) {
		    	String msg = "The project has been initialised with the template \'" +
                              templatePathName + 
                             "\' which does not now exist.";
		    	env.err.println("ERROR: " + msg);
		    	throw new ADRException(msg);
		    }
		
	    } 
	    		
		// Create the ADR title from the arguments
		StringBuilder sb = new StringBuilder();
		for (String s : adrTitleParts)
		{
		    sb.append(s);
		    sb.append(" ");
		}
		adrTitle = sb.toString().trim(); //Remove the last space
		
		System.out.println("TEMPLATE PATH = " + templatePath);
		
		// Build the record
		Record record = new Record.Builder(docsPath)
				.id(highestIndex() + 1)
				.name(adrTitle)
				.date(new Date())
				.template(templatePathName)
				.build();

		for (Integer supersedeId : supersedes) {
			// Check that a ADR with the specified ID exists, i.e. there is an ADR 
			// that can be superseded.
			if (!checkADRExists(supersedeId)) {
				String msg = "ADR to be superceded (ADR " + supersedeId + ") does not exist";
				env.err.println("ERROR: " + msg);
				throw new ADRException(msg);
			}
			record.addSupersedes(supersedeId);
		}

		//TODO check that record can handle multiple links
		
		try {
			for (String link: links) {
				int linkedToADRID = record.addLink(link);
				// Check that the ADR linked to really exists.
				if (!checkADRExists(linkedToADRID)) {
					System.err.println("ERROR: Linked to ADR (" + linkedToADRID + "), but this ADR does not exist");
					throw new ADRException("Linked to ADR (" + linkedToADRID + "), but this ADR does not exist");
				}
			}
			
		} catch (LinkSpecificationException e) {
			String msg = "ERROR: -l parameter incorrectly formed.";
			env.err.println(msg);   //TODO check that there is a test for this.
			return CommandLine.ExitCode.USAGE;  // Ensure that the usage instruction are shown
		}

		
		createADR(record);
		
		
		return exitCode;
	}

	private boolean checkADRExists(Integer adrID) throws ADRException, IOException {
		// Get the doc path
		Path rootPath = ADR.getRootPath(env);
		Path docsPath = rootPath.resolve(properties.getProperty("docPath"));
		
		// Format the ADR ID
		String formattedADRID = String.format("%04d", adrID);
		
		boolean found = false;
		try (DirectoryStream<Path>  stream = Files.newDirectoryStream(docsPath)) {
			for (Path entry: stream) {
				if (entry.getFileName().toString().startsWith(formattedADRID)) {
					found = true;
					break;
				}
			}
		}
	    
		return found;
	}

	private void createADR(Record record) throws ADRException {
		// The ADR file that is created
		Path adrPath;
		
		System.out.println("Creating ADR");

		adrPath = record.store();
		
		System.out.println("Created ADR at " + adrPath.toString());

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
