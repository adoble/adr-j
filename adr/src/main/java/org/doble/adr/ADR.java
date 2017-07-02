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

	//static private Properties properties = new Properties();  //TODO remove

	final static public int MAX_ID_LENGTH = 4;
	final static String ADR_DIR_NAME = ".adr";
	private static FileSystem fileSystem; 



	/** ADR tool main entry
	 * 
	 *
	 * @param args  Command line arguments
	 * 
	 */
	public static void main(String[] args) {

		// Instantiate the main class using the default file system
		ADR.fileSystem = FileSystems.getDefault();
		ADR adr = new ADR(); 

		// Run the commands specified in arguments.
		try {
			adr.run(args);
		}
		catch (ADRException e) {
			// Previous error handled by the commands error handling mechanism 
			// so just exit with error code
			System.exit(1);
		}
		catch (Exception e) {
			// Unexpected exception so print stack trace
			e.printStackTrace(System.err);
		}

	}

	public void run(String[] args) throws ADRException {
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
				throw new ADRException();
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


		Reflections reflections = new Reflections("org.doble.command");  // FIXME try and remove the explicit package name
		Set<Class<?>> commands = 
				reflections.getTypesAnnotatedWith(org.doble.annotations.Cmd.class);

		for(Class<?> c: commands) {
			Cmd annotation = c.getAnnotation(Cmd.class);

			commandMap.put(annotation.name(), c);
		}


		return commandMap; 

	}



	/** 
	 * Get the root directory containing the .adr directory
	 * @return Path The root directory  or null if not found
	 */
	static public Path getRootPath() throws RootPathNotFound {

		// Start in the directory where adr has been run.
		String pathName = System.getProperty("user.dir");
		Path path = ADR.getFileSystem().getPath(pathName);
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

	/** Get the file system that the adr tool uses. 
	 * @return the fileSystem
	 */
	public static FileSystem getFileSystem() {
		return fileSystem;
	}

	/** Specifiy the file system that the adr tool uses.
	 * @param fileSystem the fileSystem to set
	 */
	public static void setFileSystem(FileSystem fileSystem) {
		ADR.fileSystem = fileSystem;
	}

} // -- ADR
