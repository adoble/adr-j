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
import java.util.Optional;

import org.doble.adr.TestUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.CONCURRENT)
public class TableOfContentsTest {
    final static String tocTemplateName = "toc_template.md";
    final static String tocName = "toc.md";
    final static private Path docsPath = Path.of("project/doc/adr");
    final static private Path templatesPath = Path.of("project/templates");

    @TempDir(cleanup = CleanupMode.ALWAYS)
    Path tempDir;

    // The complete path for the ADRs
    private Path adrsDirectory;
    private Path templatesDirectory;

    @BeforeEach
    void setUp() throws Exception {

        this.adrsDirectory = tempDir.resolve(docsPath);

        this.templatesDirectory = tempDir.resolve(templatesPath);

    }

    @Test
    public void testCreation() throws Exception {

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

        TableOfContents toc = new TableOfContents(docsPath, dateFormatter);

        assertEquals(Path.of("project/doc/adr"), toc.getDocsPath());
    }

    @Test
    void testAddEntry() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
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
    void testAddEntryWithNulls() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
        TableOfContents toc = new TableOfContents(docsPath, dateFormatter);
        toc.addEntry(1, null, null);

        TocEntry expectedEntry = new TocEntry(1,
                "",
                "");

        assertEquals(expectedEntry.getId(), toc.entries.get(0).getId());
        assertEquals(expectedEntry.getFilename(), toc.entries.get(0).getFilename());
        assertEquals(expectedEntry.getTitle(), toc.entries.get(0).getTitle());

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

        Path templatePath = templatesDirectory.resolve(TableOfContentsTest.tocTemplateName);
        Files.writeString(templatePath, templateContent,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);

        // Set a test date
        Instant fixedInstant = Instant.parse("2024-10-01T00:00:00Z");
        ZoneId zoneId = ZoneId.systemDefault();
        Clock fixedClock = Clock.fixed(fixedInstant, zoneId);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

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
    void testCreatePersistentRepresentationFromResource() throws Exception {

        Path rootPath = tempDir.resolve("project");

        Path adrsPath = rootPath.resolve("doc/adr");

        Path templatesPath = rootPath.resolve("templates");

        Files.createDirectories(adrsPath);
        Files.createDirectories(templatesPath);

        // Set a test date
        Instant fixedInstant = Instant.parse("2024-10-01T00:00:00Z");
        ZoneId zoneId = ZoneId.systemDefault();
        Clock fixedClock = Clock.fixed(fixedInstant, zoneId);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
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


                * [ADR 1](0001-record-architecture-decisions.md) : Record Architecture Decisions

                * [ADR 2](0002-implement-as-Java.md) : Implement As Java

                * [ADR 3](0003-single-command-with-subcommands.md) : Single Command With Subcommands

                * [ADR 4](0004-markdown-format.md) : Markdown Format

                * [ADR 5](0005-help-comments.md) : Help Comments


                Created: 2024-10-01""";

        expectedContents = TestUtilities.trimContent(expectedContents);

        String content = Files.readString(outputPath);

        content = TestUtilities.trimContent(content);

        assertEquals(expectedContents, content);
    };

    @Test
    void testDateFormattingDefault() throws Exception {

        Files.createDirectories(this.adrsDirectory);
        Files.createDirectories(this.templatesDirectory);

        // Create a temp template in the temporary filesystems
        String templateContent = "{{date}}";

        Path templatePath = templatesDirectory.resolve(TableOfContentsTest.tocTemplateName);
        Files.writeString(templatePath, templateContent,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);

        // Set a test date
        Instant fixedInstant = Instant.parse("2024-10-01T00:00:00Z");
        ZoneId zoneId = ZoneId.systemDefault();
        Clock fixedClock = Clock.fixed(fixedInstant, zoneId);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        TableOfContents toc = new TableOfContents(adrsDirectory, dateFormatter, fixedClock);

        Path outputPath = toc.createPersistentRepresentation(Optional.of(templatePath));

        assertEquals(tempDir.resolve(Path.of("project/doc/adr/toc.md")), outputPath);

        String expectedContents = "2024-10-01";

        String content = Files.readString(outputPath);

        assertEquals(expectedContents, content);

    };

    @Test

    void testDateFormattingAlternate() throws Exception {

        Files.createDirectories(this.adrsDirectory);
        Files.createDirectories(this.templatesDirectory);

        // Create a temp template in the temporary filesystems
        String templateContent = "{{date}}";

        Path templatePath = templatesDirectory.resolve(TableOfContentsTest.tocTemplateName);
        Files.writeString(templatePath, templateContent,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);

        // Set a test date
        Instant fixedInstant = Instant.parse("2024-10-01T11:11:00Z");
        ZoneId zoneId = ZoneId.systemDefault();
        Clock fixedClock = Clock.fixed(fixedInstant, zoneId);

        // Alternate date format
        DateTimeFormatter alternativeDateFormatter = DateTimeFormatter.BASIC_ISO_DATE;

        TableOfContents toc = new TableOfContents(adrsDirectory, alternativeDateFormatter, fixedClock);

        Path outputPath = toc.createPersistentRepresentation(Optional.of(templatePath));

        assertEquals(tempDir.resolve(Path.of("project/doc/adr/toc.md")), outputPath);

        String expectedContents = "20241001";

        String content = Files.readString(outputPath);

        assertEquals(expectedContents, content);

    };

}
