package org.doble.adr;


import java.io.IOException;
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
import picocli.CommandLine.Help;
import picocli.CommandLine.Help.Column;
import picocli.CommandLine.Help.Column.Overflow;
import picocli.CommandLine.Help.TextTable;
import picocli.CommandLine.IHelpSectionRenderer;
import picocli.CommandLine.Model.UsageMessageSpec;

/**
 * Java version of the adr tool at https://github.com/npryce/adr-tools.
 * 
 * @author adoble
 *
 */
public class ADR  {

	final static public int MAX_ID_LENGTH = 4;
	final static String ADR_DIR_NAME = ".adr";
	
	public static final Integer ERRORGENERAL =      1;  // General purpose error code
	public static final Integer ERRORENVIRONMENT=   2;  // Environment variables not correctly set 
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
		EditorCommandResolver editorCommandResolver = new EditorCommandResolver();
		String editorCommand = editorCommandResolver.editorCommand();
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
				.author(determineAuthor())
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

		installEnvironmentVariablesRender(cmd);

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
			 String msg = "ERROR: The .adr directory cannot be found in this or parent directories.\n"
					 + "Has the command adr init been run?";
			 env.err.println(msg);
			 throw new ADRException(msg);
		 }

		 return rootPath.get();


	 }
	 
	 /**
	  * Get the name of the file contaning the specified id. 
	 * @param adrId The id of the ADR.
	 * @param docsPath The path containing the ADRs.
	 * @return The file name of the ADR
	 */
	static public String getADRFileName(int adrId, Path docsPath) { 
		 String fileName;

		 try { 
			 Path[] paths = Files.list(docsPath).filter(ADRFilter.filter(adrId)).toArray(Path[]::new);


			 if (paths.length == 1) { 
				 fileName = paths[0].getFileName().toString(); 
			 } 
			 else { // Gracefully fail and return an empty string 
				 fileName = ""; 
			 } 
		 } 
		 catch (IOException e) { // Gracefully fail and return an empty string 
			 fileName =	  ""; 
		 }

		 return fileName;

	 }

	static private String determineAuthor() {
		 String author = System.getenv("ADR_AUTHOR");
		 return author == null ? System.getProperty("user.name") : author;
	}

	private static final String SECTION_KEY_ENV_HEADER = "environmentVariablesHeader";
	private static final String SECTION_KEY_ENV_DETAILS = "environmentVariables";

	// https://github.com/remkop/picocli/blob/master/picocli-examples/src/main/java/picocli/examples/customhelp/EnvironmentVariablesSection.java
	private static void installEnvironmentVariablesRender(CommandLine cmd) {
		cmd.getHelpSectionMap().put(SECTION_KEY_ENV_HEADER, help -> String.format(Locale.ROOT, "Environment Variables:%n"));
		cmd.getHelpSectionMap().put(SECTION_KEY_ENV_DETAILS, new EnvironmentVariablesRenderer());
		cmd.setHelpSectionKeys(insertKey(cmd.getHelpSectionKeys()));
	}

	private static List<String> insertKey(List<String> helpSectionKeys) {
		int index = helpSectionKeys.indexOf(UsageMessageSpec.SECTION_KEY_FOOTER_HEADING);

		List<String> result = new ArrayList<>(helpSectionKeys);
		result.add(index, SECTION_KEY_ENV_HEADER);
		result.add(index + 1, SECTION_KEY_ENV_DETAILS);

		return result;
	}

	private static final class EnvironmentVariablesRenderer implements IHelpSectionRenderer {

		private final Map<String, String> env;

		EnvironmentVariablesRenderer() {
			env = new HashMap<>();
			env.put("ADR_AUTHOR", "The author of the ADR");
			env.put("ADR_EDITOR", "The editor to use to edit ADRs");
			env.put("ADR_VISUAL", "The editor to use to edit ADRs. Ignored, if ADR_EDITOR set");
		}

		@Override
		public String render(Help help) {
			if (env.isEmpty()) {
				return "";
			}

			int keyLength = env.keySet().stream()
				.mapToInt(String::length)
				.max()
				.getAsInt();

			// TextTable textTable = TextTable.forColumns(help.ansi(),
			// 	new Column(keyLength + 3, 2, Overflow.SPAN),
			// 	new Column(width(help) - (keyLength + 3), 2, Overflow.WRAP));
		
			TextTable textTable = TextTable.forColumns(help.colorScheme(),
			new Column(keyLength + 3, 2, Overflow.SPAN),
			new Column(width(help) - (keyLength + 3), 2, Overflow.WRAP));

			textTable.setAdjustLineBreaksForWideCJKCharacters(adjustCJK(help));

			for (Map.Entry<String, String> entry : env.entrySet()) {
				textTable.addRowValues(entry.getKey(), entry.getValue());
			}

			return textTable.toString();
		}

		private boolean adjustCJK(Help help) {
			return help.commandSpec().usageMessage().adjustLineBreaksForWideCJKCharacters();
		}

		private int width(Help help) {
			return help.commandSpec().usageMessage().width();
		}
	}
} // -- ADR
