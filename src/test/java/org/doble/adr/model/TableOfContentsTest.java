package org.doble.adr.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

public class TableOfContentsTest {
    final static private Path docsPath = Path.of("project/doc/adr");
    final static private DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

    final static private Path templatesPath = Path.of("project/templates");

    private Path tempDir;
    // The complete path for the adrs
    private Path adrsDirectory;
    private Path templatesDirectory;

    @BeforeEach
    void setUp() throws Exception {
        this.tempDir = Files.createTempDirectory("adr-j-test-");
        this.adrsDirectory = tempDir.resolve(docsPath);

        // Path templatesDirectory = tempDir.resolve("project/templates");
        this.templatesDirectory = tempDir.resolve(templatesPath);

    }

    @AfterEach
    void tearDown() {
        // todo

    }

    @Test
    public void testCreation() throws Exception {

        TableOfContents toc = new TableOfContents(docsPath, dateFormatter);

        assertEquals(Path.of("project/doc/adr"), toc.getDocsPath());
    }

    @Test
    void testAddEntry() {
        TableOfContents toc = new TableOfContents(docsPath, dateFormatter);

        toc.addEntry(1, "0001-record-architecture-decisions.md", "Record Architecture Decisions");
        toc.addEntry(2, "0002-implement-as-Java.md", "Implement As Java");
        toc.addEntry(3, "0003-single-command-with-subcommands.md", "Single Command With Subcommands");
        toc.addEntry(4, "0004-markdown-format.md", "Markdown Format");
        toc.addEntry(5, "0005-help-comments.md", "Help Comments");

        assertEquals(5, toc.entries.size());

        TocEntry expectedEntry = new TocEntry(3,
                "0003-single-command-with-subcommands.md",
                "Single Command With Subcommands");

        assertEquals(expectedEntry.getId(), toc.entries.get(2).getId());
        assertEquals(expectedEntry.getFilename(), toc.entries.get(2).getFilename());
        assertEquals(expectedEntry.getTitle(), toc.entries.get(2).getTitle());

    }

    @Test
    void testCreatePersistentRepresentation() throws Exception {

        Files.createDirectories(this.adrsDirectory);
        Files.createDirectories(this.templatesDirectory);

        // Create a temp template in the temporary filesystems
        String templateContent = """
                # TOC from {{date}}

                {{#each entries}}
                ADR {{id}}: {{filename}} "{{title}}"
                {{/each}}""";

        Path templatePath = templatesDirectory.resolve("toc_template.md");
        Files.writeString(templatePath, templateContent,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);

        // Set a test date
        Instant fixedInstant = Instant.parse("2024-10-01T00:00:00Z");
        ZoneId zoneId = ZoneId.systemDefault();
        Clock fixedClock = Clock.fixed(fixedInstant, zoneId);

        TableOfContents toc = new TableOfContents(adrsDirectory, dateFormatter, fixedClock);

        toc.addEntry(1, "0001-record-architecture-decisions.md", "Record Architecture Decisions");
        toc.addEntry(2, "0002-implement-as-Java.md", "Implement As Java");
        toc.addEntry(3, "0003-single-command-with-subcommands.md", "Single Command With Subcommands");
        toc.addEntry(4, "0004-markdown-format.md", "Markdown Format");
        toc.addEntry(5, "0005-help-comments.md", "Help Comments");

        Path outputPath = toc.createPersistentRepresentation(Optional.of(templatePath));

        assertEquals(tempDir.resolve(Path.of("project/doc/adr/toc.md")), outputPath);

        String expectedContents = """
                # TOC from 2024-10-01


                ADR 1: 0001-record-architecture-decisions.md "Record Architecture Decisions"

                ADR 2: 0002-implement-as-Java.md "Implement As Java"

                ADR 3: 0003-single-command-with-subcommands.md "Single Command With Subcommands"

                ADR 4: 0004-markdown-format.md "Markdown Format"

                ADR 5: 0005-help-comments.md "Help Comments"
                                """;

        String content = Files.readString(outputPath);

        assertEquals(expectedContents, content);
    };

    @Test
    @Disabled
    void testCreatePersistentRepresentationFromResource() throws Exception {

        String classpath = System.getProperty("java.class.path");

        // Path tempDir = Files.createTempDirectory("adr-j-test-");

        Path rootPath = tempDir.resolve("project");

        // Path docPath = rootPath.resolve("doc/adr");
        // Path adrsPath = fileSystem.getPath(docsPath.toString());

        Path adrsPath = rootPath.resolve("doc/adr");

        Path templatesPath = rootPath.resolve("templates");

        Files.createDirectories(adrsPath);
        Files.createDirectories(templatesPath);

        // Create a temp template in the temporary filesystems
        // String templateContent = """
        // # TOC from {{date}}

        // {{#each entries}}
        // ADR {{id}}: {{filename}} "{{title}}"
        // {{/each}}""";

        // Path templatePath = templatesPath.resolve("toc_template.md");
        // Files.writeString(templatePath, templateContent,
        // StandardOpenOption.CREATE,
        // StandardOpenOption.TRUNCATE_EXISTING,
        // StandardOpenOption.WRITE);

        // Set a test date
        Instant fixedInstant = Instant.parse("2024-10-01T00:00:00Z");
        ZoneId zoneId = ZoneId.systemDefault();
        Clock fixedClock = Clock.fixed(fixedInstant, zoneId);

        TableOfContents toc = new TableOfContents(adrsPath, dateFormatter, fixedClock);

        toc.addEntry(1, "0001-record-architecture-decisions.md", "Record Architecture Decisions");
        toc.addEntry(2, "0002-implement-as-Java.md", "Implement As Java");
        toc.addEntry(3, "0003-single-command-with-subcommands.md", "Single Command With Subcommands");
        toc.addEntry(4, "0004-markdown-format.md", "Markdown Format");
        toc.addEntry(5, "0005-help-comments.md", "Help Comments");

        Path outputPath = toc.createPersistentRepresentation(Optional.empty());

        assertEquals(tempDir.resolve(Path.of("project/doc/adr/toc.md")), outputPath);

        String expectedContents = """
                # List of ADRs

                {{#entries}}
                * [ADR {{id}}]({{filename}}) : {{title}}
                {{/entries}}
                * [ADR 1](0001-record-architecture-decisions.md) : "Record Architecture Decisions"

                * [ADR 2](0002-implement-as-Java.md) : "Implement As Java"

                * [ADR 3](0003-single-command-with-subcommands.md) : "Single Command With Subcommands"

                * [ADR 4](0004-markdown-format.md) : "Markdown Format"

                * [ADR 5](0005-help-comments.md) : "Help Comments"

                Created: 2024-10-01
                """;

        String content = Files.readString(outputPath);

        assertEquals(expectedContents, content);
    };

}
