/**
 * 
 */
package org.doble.commands;

import java.io.*;
import java.nio.file.*;
import java.text.DateFormat;
import java.util.*;

import org.doble.adr.*;
import org.doble.annotations.*;

/**
 * @author adoble
 *
 */
@Cmd(name="new",
     usage = "adr new [-s SUPERCEDED] [-l TARGET:LINK:REVERSE-LINK] TITLE_TEXT...",
     shorthelp = "Creates a new, numbered ADR.",
     help = " Creates a new, numbered ADR.  The TITLE_TEXT arguments are concatenated to" +
		" form the title of the new ADR.  The ADR is opened for editing in the" +
		" editor specified by the VISUAL or EDITOR environment variable (VISUAL is" +
		" preferred; EDITOR is used if VISUAL is not set).  After editing, the" +
		" file name of the ADR is output to stdout, so the command can be used in" +
		" scripts.\n\n" +
		" Options:\n\n" +
		" -s SUPERCEDED   A reference (number or partial filename) of a previous" +
		"                 decision that the new decision supercedes. A Markdown link" +
		"                 to the superceded ADR is inserted into the Status section." +
		"                 The status of the superceded ADR is changed to record that" +
		"                 it has been superceded by the new ADR.\n\n" +
		" -l TARGET:LINK:REVERSE-LINK" +
		"                 Links the new ADR to a previous ADR." +
		"                 TARGET is a reference (number or partial filename) of a"  +
		"                 previous decision. " +
		"                 LINK is the description of the link created in the new ADR." +
		"                 REVERSE-LINK is the description of the link created in the" +
		"                 existing ADR that will refer to the new ADR.\n\n" +
		" Multiple -s and -l options can be given, so that the new ADR can supercede"  +
		" or link to multiple existing ADRs."
		) 
public class CommandNew extends Command  {
    
	private enum CommandStates  {PARSE, SUPERCEDES, LINK, RECORD};
	ADRProperties properties = new ADRProperties();

	/**
	 * 
	 */
	public CommandNew() {
		try {
			// Load the properties
			properties.load();
			
		} catch (RootPathNotFound e) {
			System.err.println("Fatal: The .adr directory cannot be found in this or parent directories.");
			System.err.println("Has the command adr init been run?");
			System.exit(1);
		} 
		

		 
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#command(java.lang.String[])
	 */
	@Override
	public void command(String[] args) {
		String link = ""; 
		
		Record record = new Record();
				
		try {
			if (args.length == 0) {
				System.err.println("ERROR: Try giving the ADR a description.");
				System.err.println(this.getUsage());
				System.exit(1);
			} else {
				CommandStates commandState = CommandStates.PARSE;
				for (String arg : args) {

					switch (commandState) {
					case PARSE:
						if (arg.equals("-s")) {
							commandState = CommandStates.SUPERCEDES;
						}
						else if (arg.equals("-l")) {
							commandState = CommandStates.LINK;
						} else {
							commandState = CommandStates.RECORD;
							record.name += arg + " ";
						}
						break;
					case SUPERCEDES:
						record.addSupercede(Integer.parseInt(arg));
						commandState = CommandStates.PARSE;
						break;
					case LINK:
						link = arg;
						commandState = CommandStates.PARSE;
						break;
					case RECORD:
						record.name += arg + " ";
						break;
					}
				}
				if (commandState == CommandStates.SUPERCEDES) {
					System.err.println("ERROR: -s option requires an ADR reference.");
					System.out.println("Usage: " + getUsage());
					System.exit(1);
				}
				if (commandState == CommandStates.LINK) {
					System.err.println("ERROR: -l option requires an ADR reference.");
					System.out.println("Usage: " + getUsage());
					System.exit(1);
				}

			}
		} catch (NumberFormatException e) {
			System.err.println("ERROR: Invalid ADR reference for the option -s");
			System.err.println(this.getUsage());
			System.exit(1);
		}
		
		
	
		
		record.id = highestIndex() + 1;
		record.status = "New";
		record.date = DateFormat.getDateInstance().format(new Date());
		record.name = record.name.trim();
		
		try {
			record.addLink(link);
		} catch (LinkSpecificationException e) {
			System.err.println("ERROR: -l parameter incorrectly formed.");
	    	System.out.println("Usage: " + getUsage());
	    	System.exit(1);
		}	
				
		createADR(record);
	}

	//private void createADR(String adrName, Record record) {
	private void createADR(Record record) {
		
       //Save the record
		String adrFileName = "";   // The path to the file containing the ADR 
		try {
			Path docsPath = ADR.fileSystem.getPath(properties.getProperty("root"),
			                                                 properties.getProperty("docPath"));
			Path adrPath = record.store(docsPath);
			adrFileName = adrPath.toString();
		} catch (FileNotFoundException|UnsupportedEncodingException e) {
			System.err.println("FATAL: Unable to store ADR, reason: " + e.getMessage());
			System.exit(1);
		} catch (ADRNotFoundException e) {
			System.err.println("FATAL: " + e.getMessage());
		}

		
		// And now start up the editor
		String editorCommand = System.getenv("EDITOR");
		if (editorCommand == null) {
			// Try VISUAL
			editorCommand = System.getenv("VISUAL");
		}
		if (editorCommand == null) {
			System.err.println("ERROR: Editor for the ADR has not been found in the environment variables.");
			System.out.println(
					"Have you set the environment variable EDITOR or VISUAL with the editor program you want to use?");
			System.exit(1);
		}
		
		
		
		//String editorCommand = "C:/Users/adoble/AppData/Local/atom/bin/atom.cmd";
		try {
			System.out.println("Opening Editor on " + adrFileName + " ...");
			Runtime runTime = Runtime.getRuntime();
			String cmd = editorCommand + " " + adrFileName;
			//Process process = runTime.exec(cmd);
			runTime.exec(cmd);
			
		} catch (IOException e) {
			System.err.println("FATAL: Could not open the editor.");
			
		}
		
		System.out.println(adrFileName);  // Return the file name of the ADR
	}
	
	/**
	 * Find the highest index of the ADRs in the adr directory by iterating
	 * through all the files
	 * @return int The highest index found
	 */
	private int highestIndex() {
		int highestIndex = 0; 
		
		
		//Path adrPath = FileSystems.getDefault().getPath(properties.getProperty("docPath"));
		Path adrPath = ADR.fileSystem.getPath(properties.getProperty("docPath"));
		File adrDir = adrPath.toFile();
		
		FilenameFilter  filter = new ADRFilenameFilter();
		String[] fileNames = adrDir.list(filter);
        String indexStr = "";
        int index;
        for (String filename: fileNames) {
        	indexStr = filename.substring(0, ADR.MAX_ID_LENGTH);
        	index = Integer.parseInt(indexStr);
        	highestIndex = (index > highestIndex) ? index : highestIndex;                		
        }
		
		
		return highestIndex;
		
		
	}


	


}
