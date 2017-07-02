/**
 * 
 */
package org.doble.adr;

import java.io.FileInputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * @author adoble
 *
 */
public class ADRProperties extends Properties{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Properties properties;

	/**
	 * 
	 */
	public ADRProperties() {
		// TODO Auto-generated constructor stub
	}
	
	/** 
	 * Reads the .adr properties file at the root directory of the project. 
	 *
	 * @return A Properties object with the data contained in the properties file
	 * @throws RootPathNotFound is the root path does not exist. 
	 */
     public void load() throws RootPathNotFound {
		//properties = new Properties();		
		
		// Get the root directory by looking for an .adr directory
		
		Path rootPath = ADR.getRootPath(); 
		
		Path propertiesRelPath  = ADR.getFileSystem().getPath(ADR.ADR_DIR_NAME, "adr.properties");
		
		Path propertiesPath = rootPath.resolve(propertiesRelPath);
		
		try {
			if (Files.exists(propertiesPath)) {
				FileInputStream inProperties = new FileInputStream(propertiesPath.toFile());
				load(inProperties);
				inProperties.close();
			} else {
				// Set the default values. This should be stored when adr init is called, 
				setProperty("docPath", "doc/adr");
			}
		} catch (Exception  e) {
			System.err.println("FATAL: The properties file could not be read.");
			System.exit(1);
		} 
		
		
			
	}

}
