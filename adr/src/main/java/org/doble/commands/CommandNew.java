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
	ADRProperties properties;

	/**
	 * 
	 */
	public CommandNew(Environment env) throws ADRException {
		super(env);
		
		properties = new ADRProperties(env);
		try {
			// Load the properties
			properties.load();
			
		} catch (RootPathNotFound e) {
			String msg = "Fatal: The .adr directory cannot be found in this or parent directories.\n"
					+ "Has the command adr init been run?";
			throw new ADRException(msg);
					
		} 
		

		 
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see commands.Command#command(java.lang.String[])
	 */
	@Override
	public void command(String[] args) throws ADRException {
		String link = ""; 
		
		Record record = new Record();
				
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
					String msg =  "ERROR: -s option requires an ADR reference.\n";
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
		
		
	
		
		record.id = highestIndex() + 1;
		record.status = "New";
		record.date = DateFormat.getDateInstance().format(new Date());
		record.name = record.name.trim();
		
		try {
			record.addLink(link);
		} catch (LinkSpecificationException e) {
			String msg = "ERROR: -l parameter incorrectly formed.";
			msg += "Usage: " + getUsage();
	    	throw new ADRException(msg);
		}	
				
		createADR(record);
	}

	//private void createADR(String adrName, Record record) {
	private void createADR(Record record) throws ADRException {
		
       //Save the record
		String adrFileName = "";   // The path to the file containing the ADR 
		try {
			Path docsPath = env.fileSystem.getPath(properties.getProperty("root"),
			                                                 properties.getProperty("docPath"));
			Path adrPath = record.store(docsPath);
			adrFileName = adrPath.toString();
		} catch (IOException e) {
			throw new ADRException("FATAL: Unable to store ADR, reason: " + e.getMessage());
		} catch (ADRNotFoundException e) {   //TODO check the need for an extra ADR exception type
			throw new ADRException("FATAL: " + e.getMessage());
		}

		
		// And now start up the editor
	/*	String editorCommand = System.getenv("EDITOR");  // FIXME move this to the environment class
		if (editorCommand == null) {
			// Try VISUAL
			editorCommand = System.getenv("VISUAL");
		}
		if (editorCommand == null) {
			String msg = "ERROR: Editor for the ADR has not been found in the environment variables.\n"
				    	+ "Have you set the environment variable EDITOR or VISUAL with the editor program you want to use?\n";
			throw new ADRException(msg);
		}*/
		
		
		
		//String editorCommand = "C:/Users/adoble/AppData/Local/atom/bin/atom.cmd";
		// And now start up the editor
		try {
			env.out.println("Opening Editor on " + adrFileName + " ...");
			Runtime runTime = Runtime.getRuntime();
			String cmd = env.editor + " " + adrFileName;
			//Process process = runTime.exec(cmd);
			runTime.exec(cmd);
			
		} catch (IOException e) {
			throw new ADRException("FATAL: Could not open the editor.");
			
		}
		
		env.out.println(adrFileName);  // Return the file name of the ADR
	}
	
	/**
	 * Find the highest index of the ADRs in the adr directory by iterating
	 * through all the files
	 * @return int The highest index found
	 */
	private int highestIndex() {
		int highestIndex = 0; 
		
		
		Path adrPath = env.fileSystem.getPath(properties.getProperty("docPath"));
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
