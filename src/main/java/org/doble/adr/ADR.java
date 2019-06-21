package org.doble.adr;


import java.io.PrintWriter;
//import java.lang.reflect.Constructor;
import java.nio.file.*;
import java.util.*;
//import java.util.concurrent.Callable;

//import org.reflections.*;
//import org.reflections.util.ClasspathHelper;
//import org.reflections.util.ConfigurationBuilder;


import org.doble.commands.*;
//import org.doble.annotations.*;

import picocli.CommandLine;

/**
 * Java version of the adr tool at https://github.com/npryce/adr-tools.
 * 
 * @author adoble
 *
 */
public class ADR  {

	final static public int MAX_ID_LENGTH = 4;
	final static String ADR_DIR_NAME = ".adr";
	
	public static final Integer ERRORGENERAL = 1;        // General purpose error code
	public static final Integer ERRORENVIRONMENT= 2;  // Environment variables not correctly set 
	private Environment env;
	

      
	/** ADR tool main entry
	 * 
	 *
	 * @param args  Command line arguments
	 * 
	 */
	public static void main(String[] args) {
		int errorCode = 0;
		
		// Determine the editor from the system environment
		String editorCommand = null;
		editorCommand = System.getenv("EDITOR"); 
		if (editorCommand == null) {
			// Try VISUAL
			editorCommand = System.getenv("VISUAL");
		}
		// else leave as null to be picked up later
		// TODO change this to an optional variable or an entry in the configuration file
		
			
		// Set up the environment that the tool runs in with the 
		// default file system etc.
		Environment mainEnv = new Environment.Builder(FileSystems.getDefault())
				.out(System.out)
				.err(System.err)
				.in(System.in)
				.userDir(System.getProperty("user.dir"))
				.editorCommand(editorCommand)
				.editorRunner(new SystemEditorRunner()) 
				.build();
		
		errorCode = ADR.run(args, mainEnv);
		
		System.exit(errorCode);	
		

    

	}
	
	
	 static public Integer run(String[] args, Environment env) {
		Integer exitCode = 0;
		
		// Set up the command line processing and instantiate the main class using the default file system	
		// TODO use PrintWriters in the environment instead of PrintStreams
		
		CommandLine cmd = new CommandLine(new CommandADR(env))
				.setOut(new PrintWriter(env.out))
				.setErr(new PrintWriter(env.err));

		// If there are arguments then execute the subcommand
		if (args.length == 0) 
		{ 
			cmd.usage(env.out); 
		} else {
			exitCode = cmd.execute(args);
		}	
		
		return exitCode;

	}
	 
	 public Environment getEnvironment() {
		 return env;
	 }
	

	 /** 
	  * Get the root directory containing the .adr directory. 
	  * @return Path The root directory
	  * @throws ADRException Thrown if the root directory cannot be found
	  */
	 static public Path getRootPath(Environment env) throws ADRException  {
		 // NOTE: This examines the directory for the root path each time, 
		 // rather than storing the value. This is necessary to avoid 
		 // ProviderMismatchExceptions later due to the filesystems that 
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
