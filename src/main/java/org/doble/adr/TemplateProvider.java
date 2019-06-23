package org.doble.adr;

import java.io.IOException;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/** 
 * This class streams the contents if templates independent of if they are normal files or
 * are in the packaged JAR file. If the template is not specified then a default template 
 * in the resources is streamed.
 * 
 * Example use: 
 * <code>
 *      FileSystem fs = templatePath.getFileSystem();
 *      TemplateStreamer templateStreamer = new TemplateStreamer(fs, defaultTemplateFileName, env);
 *		Optional<String> templateName = Optional.of("/usr/adoble/dev/templates/my_template.md");
 *	    try (Stream<String> lines = templateStreamer.lines(Optional<String> templateName)) {
 *             lines.forEach(System.out::println);
 *       }
 * </code>
 * 
 * See https://stackoverflow.com/questions/22605666/java-access-files-in-jar-causes-java-nio-file-filesystemnotfoundexception
 *			
 * @author adoble
 *
 */

public class TemplateStreamer implements AutoCloseable {
	String defaultTemplateName;
	FileSystem fileSystem;  // The file system for the normal files
	
	FileSystem jarFileSystem = null;
	
	Stream<String> lineStream;


	/**
	 * @param fileSystem  The file system being used to get the "normal" files
	 * @param defaultTemplateFileName  The name of the resource
	 */
	public TemplateStreamer(FileSystem fileSystem, String defaultTemplateFileName) {
		super();
		this.defaultTemplateName = defaultTemplateFileName;
		this.fileSystem  = fileSystem;
	}

	/**
	 * Get a stream of lines from the template file specified in the file <code>templateFileName</code>. A stream is
	 * returned independent of if the template file is a normal file or a resource and 
	 * independent of if the resource file is packaged in a JAR or not. 
	 * Rules are: 
	 * - If the template specified is a normal file (i.e. a normal file path) then a stream to that is 
	 * returned.
	 * - If the template is defined using "rsrc:" at the start then a stream to the resource is 
	 * returned. For instance, if the template is defined as <code>rsrc:default_init_file.md</code>
	 * then a stream to the resource file (under src/main/resources) is returned, independent of 
	 * if the resource file is in a JAR or not. 
	 * - If a template is not specified (the optional field template is empty), then a stream to the default 
	 * template (defined in ADRProperties) is given, independent of 
	 * if the resource file is in a JAR or not.
	 */
   Stream<String> lines(Optional<String> templateFileName) throws IOException, URISyntaxException{
	   
	   Path templatePath;
		if (templateFileName.isPresent() && !templateFileName.get().substring(0,5).equals("rsrc:")) {
			// Template has been specified by user and is a normal file
			templatePath =  fileSystem.getPath(templateFileName.get());
			lineStream = Files.lines(templatePath);
		} else if (templateFileName.isPresent() && templateFileName.get().substring(0,5).equals("rsrc:")) {
			// Template is user specified resource.
			String templateResourceName = templateFileName.get().substring(5, templateFileName.get().length()); // Remove the 'resource" indicator
			
			templatePath = getTemplatePath(templateResourceName);
			lineStream = Files.lines(templatePath);
			
		} else {
			// Template has not been specified so get an input stream to the default template resource
			templatePath = getTemplatePath(ADRProperties.defaultTemplateName);
			lineStream = Files.lines(templatePath);
		}
		
		return lineStream;
 
   }


private Path getTemplatePath(String templateFileName) throws URISyntaxException, IOException {
	Path templatePath;
	URI uri = ClassLoader.getSystemResource(templateFileName).toURI();
	
	// Now check if the system resource is either a normal file (e.g. we are running in an IDE) or
	// the system resource is is a packaged JAR (e.g. we are running a packaged JAR file) 
	if (uri.getScheme().equalsIgnoreCase("jar")) {
		// Running as a packaged JAR so set uo a file system to read this jar using the first 
		// part of the URI (seperated with '!') as the file to use
		String[] uriParts = uri.toString().split("!");
		jarFileSystem = FileSystems.newFileSystem(URI.create(uriParts[0]), new HashMap<>());
		templatePath = jarFileSystem.getPath(uriParts[1]);
	} else {
		// Assume that the system resource is a normal file (e.g. we are running in an IDE). 
		templatePath = fileSystem.getPath(templateFileName);
	}
	return templatePath;
}
   
   Stream<String>  lines(String templateFileName) throws IOException, URISyntaxException {
	   return lines(Optional.ofNullable(templateFileName));
    }
   
   Stream<String> lines() throws IOException, URISyntaxException {
	   return lines(Optional.of(defaultTemplateName));
   }

	@Override
	public void close() throws IOException {
		if (jarFileSystem != null) jarFileSystem.close();
		lineStream.close();

	}

}
