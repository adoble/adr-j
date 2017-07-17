package org.doble.adr;


import java.lang.reflect.Constructor;
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

	final static public int MAX_ID_LENGTH = 4;
	final static String ADR_DIR_NAME = ".adr";
	private static FileSystem fileSystem; 

	
	

    public ADR() {}
    
      
	/** ADR tool main entry
	 * 
	 *
	 * @param args  Command line arguments
	 * 
	 */
	public static void main(String[] args) {

		// Instantiate the main class using the default file system
		ADR.fileSystem = FileSystems.getDefault();
		
		// Determine the editor
		String editorCommand = System.getenv("EDITOR"); 
		if (editorCommand == null) {
			// Try VISUAL
			editorCommand = System.getenv("VISUAL");
		}
		if (editorCommand == null) {
			String msg = "ERROR: Editor for the ADR has not been found in the environment variables.\n"
				    	+ "Have you set the environment variable EDITOR or VISUAL with the editor program you want to use?\n";
			System.exit(1);
		} 
		
		

		
		// Set up the environment that the tool runs in. 
		Environment env = new Environment.Builder(FileSystems.getDefault())
				.out(System.out)
				.err(System.err)
				.in(System.in)
				.userDir(System.getProperty("user.dir"))
				.editor(editorCommand)
				.build();
		
		ADR adr = new ADR();   //TODO env -> ADR constructor

		// Run the commands specified in arguments.
		try {
			adr.run(args, env);
		}
		catch (ADRException e) {
			env.err.println(e.getMessage());
			System.exit(1);
		}
		catch (Exception e) {
			// Unexpected exception so print stack trace
			e.printStackTrace(System.err);
		}

	}

	public void run(String[] args, Environment env) throws ADRException {
		Map<String, Class<?>> commandMap; 
		Command commandNull = new CommandNull(env);
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
				Constructor<Command> ctor = (Constructor<Command>) commandClass.getConstructor(Environment.class);
				command = ctor.newInstance(env);
				// Execute the command
				command.command(subCmdArgs);
			} 
			catch (NoSuchMethodException e) {
				throw new ADRException("FATAL: Unknown command [" + args[0] + ". Use\n   adr help \nfor more information.");
			}
			catch (ADRException e) {
				throw e;
			}
			catch(Exception e) {
				throw new ADRException("FATAL: Internal error.", e);
			}
		} else {
			env.out.println("ERROR: Specify a command. For instance:");
			Set<String> keys = commandMap.keySet();
			for (String commandName : keys) {
				env.out.println("   " + commandName);
			}
		}
	}

	static public Map<String, Class<?>> buildCommandMap (String packageName)  {
		HashMap<String, Class<?>> commandMap = new HashMap<String, Class<?>>();


		Reflections reflections = new Reflections("org.doble.command");  // TODO try and remove the explicit package name
		Set<Class<?>> commands = 
				reflections.getTypesAnnotatedWith(org.doble.annotations.Cmd.class);

		for(Class<?> c: commands) {
			Cmd annotation = c.getAnnotation(Cmd.class);

			commandMap.put(annotation.name(), c);
		}


		return commandMap; 

	}



	/** 
	 * Get the root directory containing the .adr directory. 
	 * @return Path The root directory
	 * @throws ADRException Thrown if the root directory cannot be found
	 */
	static public Path getRootPath(Environment env) throws ADRException  {
		// NOTE: This examines the directory for the root path each time, 
		// rather than storing the value. This is necessary to avoid 
		// ProviderMismatchExceptios later due to the filesystems that 
		// created the Path being different. 
		
		// The directory containing the .adr directory, 
		// i.e. the root of the project 
		Optional<Path> rootPath = Optional.empty(); 
			
		// Find the root path, starting in the directory 
			// where the ADR tool has been run.
			Path path = env.dir;
			
			Path adrFilePath; 
			while (path != null) {
				adrFilePath = path.resolve(ADR.ADR_DIR_NAME);

				if (Files.exists(adrFilePath)) {
					rootPath = Optional.of(path);
					break;
				} else {
					// Check the directory above 
					path = path.getParent();
				}
			}
		
		
		if (!rootPath.isPresent()) {
			String msg = "FATAL: The .adr directory cannot be found in this or parent directories.\n"
					+ "Has the command adr init been run?";
			throw new ADRException(msg);
		}
		
		return rootPath.get();
		
	
	}



} // -- ADR
