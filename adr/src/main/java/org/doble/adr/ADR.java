package org.doble.adr;

import java.io.*;
import java.lang.reflect.Constructor;
import java.net.*;
import java.nio.file.*;
import java.util.*;

import org.reflections.*;

import org.doble.commands.*;
import org.doble.annotations.*;

/**
 * Java version of the adr tool at https://github.com/npryce/adr-tools
 * 
 * @author adoble
 *
 */
public class ADR   {

	static private Properties properties = new Properties();
	
	final static public int MAX_ID_LENGTH = 4;
	final static String ADR_DIR_NAME = ".adr";
	


	/**
	 * @param args
	 * 
	 * 
	 * TODO  Refactor from static class to object.
	 */
	public static void main(String[] args) {
		
		// Instantiate the main class. 
		ADR adr = new ADR(); 
		
		// Run the commands specified in arguments.
		adr.run(args);
	
	}

	private  void run(String[] args) {
		Map<String, Class<?>> commandMap; 
		Command commandNull = new CommandNull();
		Command command = commandNull;

		// Build the map of the adr commands keyed with the command name.
		// All the commands are in the specified package. 
		commandMap = buildCommandMap("org.doble.commands");
		
		// Run the specified command
		if (args.length > 0) {
			String[] subCmdArgs = Arrays.copyOfRange(args, 1, args.length);

			try {
				Class<?> commandClass = commandMap.get(args[0]);
				// Create the command object
				@SuppressWarnings("unchecked")
				Constructor<Command> ctor = (Constructor<Command>) commandClass.getConstructor();
				command = ctor.newInstance();
				// Execute the command
				command.command(subCmdArgs);
			} catch (Exception e) {
				System.out.println("FATAL: Unknown command. Use\n   adr help \nfor more information. ");
				System.exit(1);
			}
		} else {
			System.out.println("ERROR: Specify a command. For instance:");
			Set<String> keys = commandMap.keySet();
			for (String commandName : keys) {
				System.out.println("   " + commandName);
			}
		}
	}
	
	static public Map<String, Class<?>> buildCommandMap (String packageName)  {
	    //String commandName; // FIXME Remove
	    //String relPath = packageName.replace('.', '/');   // FIXME Remove
	    HashMap<String, Class<?>> commandMap = new HashMap<String, Class<?>>();
	    
	   
	    
	    //URL resource = Thread.currentThread().getContextClassLoader().getResource(relPath);
	    //URL resource = getClass().getClassLoader().getResource(relPath);
	    
	    
		Reflections reflections = new Reflections("org.doble.command");  // FIXME try and remove the explicit package name
		Set<Class<?>> commands = 
			    reflections.getTypesAnnotatedWith(org.doble.annotations.Cmd.class);
		
		for(Class<?> c: commands) {
			Cmd annotation = c.getAnnotation(Cmd.class);
			
			commandMap.put(annotation.name(), c);
		    }


		return commandMap; 
		
		
	}

	static public Properties loadProperties() throws RootPathNotFound {
		properties = new Properties();		
		
		// Get the root directory by looking for an .adr directory
		
		Path rootPath = getRootPath(); 
		
		Path propertiesRelPath  = FileSystems.getDefault().getPath(ADR.ADR_DIR_NAME, "adr.properties");
		
		Path propertiesPath = rootPath.resolve(propertiesRelPath);
		
		try {
			if (Files.exists(propertiesPath)) {
				FileInputStream inProperties = new FileInputStream(propertiesPath.toFile());
				properties.load(inProperties);
				inProperties.close();
			} else {
				// Set the default values. This should be stored when adr init is called, 
				properties.setProperty("docPath", "doc/adr");
			}
		} catch (Exception  e) {
			System.err.println("FATAL: The properties file could not be read.");
			System.exit(1);
		} 
		
		return properties;
		
		
	}
	
	/** 
	 * Get the root directory containing the .adr directory
	 * @return Path The root directory  or null if not found
	 */
	static private Path getRootPath() throws RootPathNotFound {
		
		// Start in the directory where adr has been run.
		String pathName = System.getProperty("user.dir");
		Path path = FileSystems.getDefault().getPath(pathName);
        Path adrFilePath; 
		while (path != null) {
			adrFilePath = path.resolve(ADR.ADR_DIR_NAME);
			
			if (Files.exists(adrFilePath)) {
				return path; 

			} else {
				// Check the directory above 
				path = path.getParent();
			}
		}
		
		throw new RootPathNotFound();
		
	}

	

} // -- ADR
