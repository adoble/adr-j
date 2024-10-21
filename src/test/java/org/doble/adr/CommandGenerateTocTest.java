package org.doble.adr;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

// Not that tests for this class/command do not use the Jimfs 
// in-memory file system, but instead use the Junit features to 
// crate temporary directories and files. This is due to the 
// handlebars templating library not being to use this.
public class CommandGenerateTocTest {

	final static private Path rootPath = Path.of("project");

	private Environment env;

	final static String tocTemplateName = "toc_template.md";
	final static String tocName = "toc.md";

	final static private Path templatesPath = Path.of("project/templates");

	@TempDir(cleanup = CleanupMode.NEVER)
	Path tempDir;

	// The complete path for the ADRs
	private Path adrsDirectory;
	private Path templatesDirectory;
	private Path rootDirectory;

	private String[] adrTitles = { "another test architecture decision",
			"yet another test architecture decision",
			"and still the adrs come",
			"to be superseded",
			"some functional name",
			"something to link to",
			"a very important decision" };

	@BeforeEach
	public void setUp() throws Exception {

		this.templatesDirectory = tempDir.resolve(templatesPath);
		this.rootDirectory = tempDir.resolve(rootPath);

		Files.createDirectories(this.templatesDirectory);

		env = new Environment.Builder(FileSystems.getDefault())
				.out(System.out)
				.err(System.err)
				.in(System.in)
				.userDir(rootDirectory)
				.editorCommand("dummyEditor")
				.editorRunner(new TestEditorRunner())
				.build();

		// Set up the directory structure
		String[] args = { "init" };
		ADR.run(args, env);

		// Now set up some ADRs for test purposes
		for (String title : adrTitles) {
			args = TestUtilities.argify("new " + title);
			ADR.run(args, env);
		}

	}

	@AfterEach
	public void tearDown() throws Exception {

	}

	// Default TOC template in resources.
	@Test
	void testSimpleCommand() throws Exception {

		String[] args = { "generate", "toc" };
		int exitCode = ADR.run(args, env);
		assertEquals(0, exitCode);

		Path tocPath = tempDir.resolve("project/doc/adr/toc.md");

		// Check if the TOC file has been created
		assertTrue(Files.exists(tocPath));

		// Sample check the expected contents
		String expectedSample = "* [ADR 6](0006-some-functional-name.md) : some functional name";

		String actual = Files.readString(tocPath);

		assert (actual.contains(expectedSample));

	}

	@Test
	void testCommandWithTemplateOption() throws Exception {

		// Create a template for test
		String testTemplateContent = """
				# ADR files
				Test template

				{{#entries}}
				* ADR {{id}} : {{filename}}
				{{/entries}}
				""";

		Path testTemplatePath = tempDir.resolve(templatesPath).resolve("test_template.md");
		Files.createFile(testTemplatePath);
		Files.writeString(testTemplatePath, testTemplateContent);

		String[] args = { "generate", "toc", "-t", testTemplatePath.toString() };

		int exitCode = ADR.run(args, env);
		assertEquals(0, exitCode);

		Path tocPath = tempDir.resolve("project/doc/adr/toc.md");

		// Check if the TOC file has been created
		assertTrue(Files.exists(tocPath));

		// Sample check the expected contents
		String expectedSample1 = "Test template";
		String expectedSample2 = "* ADR 6 : 0006-some-functional-name.md";

		String actual = Files.readString(tocPath);

		assert (actual.contains(expectedSample1));
		assert (actual.contains(expectedSample2));

	}

	@Test
	void testCommandWithTemplateInProperties() throws Exception {
		// Create a template for test
		String testTemplateContent = """
				# ADR files
				Test template in properties

				{{#entries}}
				* ADR {{id}} : {{filename}}
				{{/entries}}
				""";

		Path testTemplatePath = tempDir.resolve(templatesPath).resolve("test_template.md");
		Files.createFile(testTemplatePath);
		Files.writeString(testTemplatePath, testTemplateContent);

		// Now add the template to to the properties
		String[] argsConfig = { "config", "tocTemplateFile", testTemplatePath.toString() };
		int exitCode = ADR.run(argsConfig, env);
		assertEquals(0, exitCode);

		// Now run the generate toc command without specifing the template
		String[] argsGenerate = { "generate", "toc" };
		exitCode = ADR.run(argsGenerate, env);
		assertEquals(0, exitCode);

		Path tocPath = tempDir.resolve("project/doc/adr/toc.md");

		// Check if the TOC file has been created
		assertTrue(Files.exists(tocPath));

		// Sample check the expected contents
		String expectedSample1 = "Test template in properties";
		String expectedSample2 = "* ADR 6 : 0006-some-functional-name.md";

		String actual = Files.readString(tocPath);

		assert (actual.contains(expectedSample1));
		assert (actual.contains(expectedSample2));

	}

	// Should use the template default in resources
	@Test
	void testCommandWithNoTemplateSpecified() throws Exception {

		// Run the generate toc command without specifing the template
		String[] args = { "generate", "toc" };
		int exitCode = ADR.run(args, env);
		assertEquals(0, exitCode);

		Path tocPath = tempDir.resolve("project/doc/adr/toc.md");

		// Check if the TOC file has been created
		assertTrue(Files.exists(tocPath));

		// Sample check the expected contents
		String expectedSample1 = "# List of ADRs";
		String expectedSample2 = "* [ADR 6](0006-some-functional-name.md) : some functional name";

		String actual = Files.readString(tocPath);

		assert (actual.contains(expectedSample1));
		assert (actual.contains(expectedSample2));

	}

	@Test
	void testCommandWithNonExistingTemplateFile() {

		// Run the generate toc command witha non-existing template
		String[] argsGenerate = { "generate",
				"toc",
				"-template",
				tempDir.resolve("templates/template.md").toString()
		};
		int exitCode = ADR.run(argsGenerate, env);
		assertEquals(CommandLine.ExitCode.SOFTWARE, exitCode);

	}

	@Test
	void testCommandWhenNoADRsCreated() {
		fail("TO DO");
	}

}
