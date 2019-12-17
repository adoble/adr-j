package org.doble.commands;

import java.nio.file.*;
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
	
	/*
	 * BASIC_ISO_DATE 	Basic ISO date 	'20111203'
ISO_LOCAL_DATE 	ISO Local Date 	'2011-12-03'
ISO_OFFSET_DATE 	ISO Date with offset 	'2011-12-03+01:00'
ISO_DATE 	ISO Date with or without offset 	'2011-12-03+01:00'; '2011-12-03'
ISO_LOCAL_TIME 	Time without offset 	'10:15:30'
ISO_OFFSET_TIME 	Time with offset 	'10:15:30+01:00'
ISO_TIME 	Time with or without offset 	'10:15:30+01:00'; '10:15:30'
ISO_LOCAL_DATE_TIME 	ISO Local Date and Time 	'2011-12-03T10:15:30'
ISO_OFFSET_DATE_TIME 	Date Time with Offset 	2011-12-03T10:15:30+01:00'
ISO_ZONED_DATE_TIME 	Zoned Date Time 	'2011-12-03T10:15:30+01:00[Europe/Paris]'
ISO_DATE_TIME 	Date and time with ZoneId 	'2011-12-03T10:15:30+01:00[Europe/Paris]'
ISO_ORDINAL_DATE 	Year and day of year 	'2012-337'
ISO_WEEK_DATE 	Year and Week 	2012-W48-6'
ISO_INSTANT 	Date and Time of an Instant 	'2011-12-03T10:15:30Z'
RFC_1123_DATE_TIME 	RFC 1123 / RFC 822 	'Tue, 3 Jun 2008 11:05:30 GMT'
	 */
	private enum DateFormatterType {
		BASIC_ISO_DATE,
		ISO_LOCAL_DATE,
		ISO_OFFSET_DATE,
		ISO_DATE,
		ISO_LOCAL_TIME,
		ISO_OFFSET_TIME,
		ISO_TIME,
		ISO_LOCAL_DATE_TIME,
		ISO_OFFSET_DATE_TIME,
		ISO_ZONED_DATE_TIME,
		ISO_DATE_TIME,
		ISO_ORDINAL_DATE,
		ISO_WEEK_DATE,
		ISO_INSTANT,
		RFC_1123_DATE_TIME 
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
	 * TODO Extend this with subcommands for each of the properties 
	 * that can be set.
	 * 
	 * @see commands.Command#command(java.lang.String[])
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
	void docsPath(@Parameters(paramLabel = "<docsPath>") String docsPath) throws Exception {
		
		ADRProperties properties = loadProperties();
	
			
		properties.setProperty("docsPath", docsPath);
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
