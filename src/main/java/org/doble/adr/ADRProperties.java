/**
 * 
 */
package org.doble.adr;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.io.BufferedReader;;

/**
 * @author adoble
 *
 */
public class ADRProperties extends Properties{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	Environment env;

	/**
	 * @param env The environment in which the tool is run in
	 */
	public ADRProperties(Environment env) {
		
		this.env = env;
	}
	
	/** 
	 * Reads the .adr properties file at the root directory of the project. 
	 *
	 * @return A Properties object with the data contained in the properties file
	 * @throws RootPathNotFound is the root path does not exist. 
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
				setProperty("docPath", "doc/adr");
			}
		} catch (Exception e) {
			throw new ADRException("FATAL: The properties file could not be read.", e);
		} 
		
		
			
	}

}
