/**
 * 
 */
package org.doble.commands;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

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
		Path rootPath = ADR.getRootPath(env);
		Path docsPath = rootPath.resolve(properties.getProperty("docPath"));

		try (Stream<Path> stream = Files.list(docsPath)){
			stream.filter(ADRFilter.filter()).forEachOrdered(env.out::println);
		} catch (IOException e) {
			throw new ADRException("FATAL: Cannot access directory.", e);
		}

		//			File docsDir = docsPath.toFile();
		//			
		//			FilenameFilter filter = new ADRFilenameFilter();
		//			
		//			for (String adrFileName: docsDir.list(filter)) {
		//				env.out.println(adrFileName);
		//			}

	}



}
