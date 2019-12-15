/**
 * 
 */
package org.doble.adr;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;;

/**
 * @author adoble
 *
 */
public class ADRProperties extends Properties{
	/**
	 * 
	 */
	public static final String defaultDocPath = "doc/adr";  //TODO is this the right place for this constant?
	public static final String defaultTemplateName = "default_template.md";
	public static final String defaultInitialTemplateName = "default_initial_template.md";
	public static final String defaultDateFormat = "ISO_LOCAL_DATE";

	private static final long serialVersionUID = 1L;
	
	


	Environment env;

	/**
	 * @param env The environment in which the tool is run in
	 */
	public ADRProperties(Environment env) {
		
		this.env = env;
	}
	
	/** 
	 * Reads the adr.properties file and sets the properties. 
	 *
	 * @throws ADRException if the properties file cannot be read 
	 */
	public void load() throws ADRException {
		//properties = new Properties();		

		// Get the root directory by looking for an .adr directory

		Path rootPath = env.dir; 

		Path propertiesRelPath  = env.fileSystem.getPath(ADR.ADR_DIR_NAME, "adr.properties");

		Path propertiesPath = rootPath.resolve(propertiesRelPath);

		try {
			if (Files.exists(propertiesPath)) {
				BufferedReader propertiesReader = Files.newBufferedReader(propertiesPath);
				load(propertiesReader);
				propertiesReader.close();
			} else {
				// Set the default values. This should be stored when adr init is called, 
				setProperty("docPath", defaultDocPath);
				setProperty("dateFormat", defaultDateFormat);
			}
		} catch (Exception e) {
			throw new ADRException("FATAL: The properties file could not be read.", e);
		} 
	}


		/** 
		 * Stores the properties in the adr.properties file.
		 * 
		 */
		public void store() throws ADRException {
			Path rootPath = env.dir; 

			Path propertiesRelPath  = env.fileSystem.getPath(ADR.ADR_DIR_NAME, "adr.properties");
			Path propertiesPath = rootPath.resolve(propertiesRelPath);
							
			// Create a properties file if required. 
			if (!Files.exists(propertiesPath)) {
				// Create a properties file
				try {
					Files.createDirectories(propertiesPath.getParent());
					Files.createFile(propertiesPath);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					throw new ADRException("FATAL: The properties file could not be created.", e);
				}
			}

			// Now save the properties
			try (BufferedWriter propertiesWriter = Files.newBufferedWriter(propertiesPath))
			{
				store(propertiesWriter, null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				throw new ADRException("FATAL: The properties file could not be written to.", e);
			}

		}

}
