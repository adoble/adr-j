/**
 * 
 */
package org.doble.commands;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.*;

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
	private ADRProperties properties; 

	/**
	 * 
	 */
	public CommandList(Environment env) throws ADRException {
		super(env);
		
		properties = new ADRProperties(env);
			
		// Load the properties
		properties.load();
		
	}

	/* (non-Javadoc)
	 * @see commands.Command#command(java.lang.String[])
	 */
	@Override
	public void command(String[] args) throws ADRException {
		
		try {
			Path docsPath = env.fileSystem.getPath(properties.getProperty("root"),
			                                                 properties.getProperty("docPath"));
			
			File docsDir = docsPath.toFile();
			
			FilenameFilter filter = new ADRFilenameFilter();
			
			for (String adrFileName: docsDir.list(filter)) {
				env.out.println(adrFileName);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new ADRException("FATAL: The ADR could not be saved!");
		}  
		

	}



}
