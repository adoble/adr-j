/**
 * 
 */
package org.doble.commands;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Properties;

import org.doble.adr.*;
import org.doble.annotations.Cmd;

/**
 * @author adoble
 *
 */
@Cmd (
		name="list",
		usage="adr list",
		shorthelp="Lists the architecture decision records",
		help= "Lists the architecture decision records"
		)
public class CommandList extends Command {
	private ADRProperties properties = new ADRProperties(); 

	/**
	 * 
	 */
	public CommandList() {
		try {
			// Load the properties
			properties.load();
			
		} catch (RootPathNotFound e) {
			System.err.println("Fatal: The .adr directory cannot be found in this or parent directories.");
			System.err.println("Has the command adr init been run?");
			System.exit(1);
		} 
	}

	/* (non-Javadoc)
	 * @see commands.Command#command(java.lang.String[])
	 */
	@Override
	public void command(String[] args) {
		
		try {
			Path docsPath = FileSystems.getDefault().getPath(properties.getProperty("root"),
			                                                 properties.getProperty("docPath"));
			
			File docsDir = docsPath.toFile();
			
			FilenameFilter filter = new ADRFilenameFilter();
			
			for (String adrFileName: docsDir.list(filter)) {
				System.out.println(adrFileName);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("FATAL: The ADR could not be saved!");
			System.exit(1);
		}  
		

	}



}
