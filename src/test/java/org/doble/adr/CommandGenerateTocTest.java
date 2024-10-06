package org.doble.adr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import picocli.CommandLine;

import org.doble.adr.model.TableOfContents;
import org.doble.commands.CommandGenerateToc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class CommandGenerateTocTest {

	final static private Path rootPathName = Path.of("/project");
	// Where the adrs are stored
	final static private Path docsPath = Path.of("/project/doc/adr");
	private static FileSystem fileSystem;
	private Environment env;

	private String[] adrTitles = { "another test architecture decision",
			"yet another test architecture decision",
			"and still the adrs come",
			"to be superseded",
			"some functional name",
			"something to link to",
			"a very important decision" };

	@BeforeEach
	public void setUp() throws Exception {
		Path rootPath = null;

		// Set up the mock file system
		fileSystem = Jimfs.newFileSystem(Configuration.unix());

		rootPath = fileSystem.getPath("/project");

		Files.createDirectory(rootPath);

		env = new Environment.Builder(fileSystem)
				.out(System.out)
				.err(System.err)
				.in(System.in)
				.userDir(rootPathName)
				.editorCommand("dummyEditor")
				.editorRunner(new TestEditorRunner())
				.build();

		// Set up the directory structure
		String[] args = { "init" };
		ADR.run(args, env);

	}

	@AfterEach
	public void tearDown() throws Exception {
		fileSystem.close();
	}

}
