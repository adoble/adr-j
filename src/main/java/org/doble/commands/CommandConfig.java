package org.doble.commands;

import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Set;
import java.util.concurrent.Callable;

import org.doble.adr.*;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

/**
 * Subcommand to configure the properties
* 
 * @author adoble
 *
 */

@Command(name = "config",
description = "List of the currently set properties.")
public class CommandConfig  implements Callable<Integer> {
	@ParentCommand
	CommandADR commandADR;
	
	private Environment env;
	private ADRProperties properties; 
	
    // The values used to specify how the dates are formatted in ADRs
	// TODO These values are repeated in CommandNew. Refactor so that they are only specified once. 
	private enum DateFormatterType {
		BASIC_ISO_DATE,   // Basic ISO date 	'20111203'
		ISO_DATE,         // ISO Date with or without offset 	'2011-12-03+01:00'; '2011-12-03'
		ISO_LOCAL_DATE,   // ISO Local Date 	'2011-12-03'
		ISO_OFFSET_DATE,  // Time with offset 	'10:15:30+01:00'
		ISO_ORDINAL_DATE, // Year and day of year 	'2012-337'
		ISO_WEEK_DATE,    // Year and Week 	2012-W48-6'
		SHORT,            // Short text style, typically numeric
		MEDIUM,           // Medium text style, with some detail.
		LONG,             // Long text style, with lots of detail.
		FULL	          // Full text style, with the most detail.
	}
	

	
	
	/**
	 * 
	 */
	public CommandConfig()  {

	}
	
	/* The commmand 
	 *   <code>adr config </code>
	 * lists the currently set properties in the properties file. 
	 * 
	 * It take the subcommands author, docPath, templateFile and dateFormat. 
	 * 
	 */
	@Override
	public Integer call() throws Exception {
		
		ADRProperties properties = loadProperties();
		
		Set<String> propertyKeys= properties.stringPropertyNames();
		String propStr = "";
		for (String key : propertyKeys) {
			propStr = key + "=" + properties.getProperty(key);
			env.out.println(propStr);
		}
		        
		return 0;
	}

	@Command(description = "Change the default name of the ADR author.") 
	void author(@Parameters(paramLabel = "<author>") String[] author) throws Exception {
		
		ADRProperties properties = loadProperties();
	
		String authorFullName = ""; 
		for (String	authorPart : author) 
		{ 
			authorFullName += authorPart + " "; 
		}
		authorFullName = authorFullName.trim(); 
		
		properties.setProperty("author", authorFullName);
		properties.store();
		
		
	}
	
	@Command(description = "Change the location where future ADRs are stored.") 
	void docPath(@Parameters(paramLabel = "<docPath>") String docPathName) throws Exception {
		
		ADRProperties properties = loadProperties();
		
		// Check if the directory exists and if not create it. 
		Path docPath = env.dir.resolve(docPathName);
		if (!Files.exists(docPath)) {
			Files.createDirectories(docPath);
		}
				
		properties.setProperty("docPath", docPathName);
		properties.store();
	}
	
	@Command(description = "Change how the dates in the ADRs are represented.") 
	void dateFormat(@Parameters(paramLabel = "<dateFormat>") DateFormatterType dateFormatterType) throws Exception {
		
		ADRProperties properties = loadProperties();
	
			
		properties.setProperty("dateFormat", dateFormatterType.name());
		properties.store();
	}
	
	@Command(description = "Change the template file used to created ADRs.") 
	void templateFile(@Parameters(paramLabel = "<templateFile>") String templateFile) throws Exception {
		
		ADRProperties properties = loadProperties();
	
		properties.setProperty("templateFile", templateFile);
		properties.store();
	}

	private ADRProperties loadProperties()  throws ADRException {
		env = commandADR.getEnvironment();
		
		properties = new ADRProperties(env);
					
		// Load the properties
		properties.load();
		
		return properties;
		
	}

	
	

}
