/**
 * Environment for the ADR tool
 * 
 * Usage: 
 *   <code> 
 *   return new Environment.Builder(fileSystem)
 *                                 .out(os)
 *                                 .in(is)
 *                                 .err(errs)
 *                                 .userDir("/root/user/ad/working");
 *                                 
 *   PrintStream outStream = env.out;
 *                                 
 *                                 
 *  </code>
 *  
 *
 *   
 */
package org.doble.adr;

import java.io.*;
import java.nio.file.*;

/**
 * @author adoble
 *
 */
public class Environment {

	/** The file system that is being used */
	public final FileSystem fileSystem;
	
	/** The  output stream used for this program. */
	public final PrintStream out;
	
	/** The  error  stream used for this program. */
	public final PrintStream err;
	
	/** The  input stream used for this program. */
	public final InputStream in;
	
	/** The directory where the tool is running */
	public final Path dir; 
	
	/** The command line to run the editor used for editing the ADRs */
	public final String editor;  
	
	/** The runner used for firing up the editor */
	public final EditorRunner editorRunner;

	/**
	 * Private Constructor so that only the builder can be used to 
	 * construct the class.
	 * @param builder The builder used
	 */
	private Environment(Builder builder) {
		this.fileSystem = builder.fileSystem;
		this.out = builder.outStream;
		this.err = builder.errStream;
		this.in = builder.inStream;
		this.dir = builder.currentDirectory;
		this.editor = builder.editorCommand;
		this.editorRunner = builder.editorRunner;
		
	}



	public static class Builder {
		private FileSystem fileSystem;
		private PrintStream outStream;
		private PrintStream errStream;
		private InputStream inStream;
		private Path currentDirectory;
		private String editorCommand;
		private EditorRunner editorRunner;

		public Builder(FileSystem fileSystem) {
			this.fileSystem = fileSystem;
		}

		public Builder out(PrintStream outStream) {
			this.outStream = outStream;
			return this;
		}

		public Builder err(PrintStream errStream) {
			this.errStream = errStream;
			return this;
		}

		public Builder in(InputStream inStream) {
			this.inStream = inStream;
			return this;
		}

		public Builder userDir(Path currentDirectory) {
			this.currentDirectory = currentDirectory;
			return this;
		}

		public Builder userDir(String currentDirectory) {
			this.currentDirectory = fileSystem.getPath(currentDirectory);
			return this;
		}
         
		public Builder editor(String editorCommand) {
			this.editorCommand = editorCommand;
			return this;
		}
		
		public Builder editorRunner(EditorRunner editorRunner) {
			this.editorRunner = editorRunner;
			return this;
		}
		
		public Environment build() {
			return new Environment(this);
		}

	}

}
