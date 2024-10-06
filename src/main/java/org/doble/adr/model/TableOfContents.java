package org.doble.adr.model;

import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.doble.adr.ADRException;

import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TableOfContents {
    private static final String TOC_FILE_NAME = "toc";
    // private static final DateTimeFormatter YEAR_FORMATTER =
    // DateTimeFormatter.ofPattern("yyyy");
    private final LocalDate date;

    private final DateTimeFormatter dateFormatter;

    // Where the TOC file is stored
    private final Path docsPath;

    // All the entries in the table of contents,
    ArrayList<TocEntry> entries = new ArrayList<>();

    // Primary constructor
    public TableOfContents(Path docsPath, DateTimeFormatter dateFormatter) {
        // Delegate to the other constructor with a default clock
        this(docsPath, dateFormatter, Clock.systemDefaultZone());
    }

    // Constructor with Clock
    public TableOfContents(Path docsPath, DateTimeFormatter dateFormatter, Clock clock) {
        this.date = LocalDate.now(clock); // Use the clock specified
        this.docsPath = docsPath;
        this.dateFormatter = dateFormatter;
    }

    public Path getDocsPath() {
        return this.docsPath;
    }

    public void addEntry(int id, String filename, String title) {
        TocEntry entry = new TocEntry(id, filename, title);
        entries.add(entry);
    }

    public List<TocEntry> getEntries() {
        return entries;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public Path createPersistentRepresentation(Optional<Path> templatePath) throws ADRException {
        TemplateLoader templateLoader;
        String extension;
        String basename;

        if (templatePath.isPresent()) {
            // String name = templatePath.get().toString();
            // String parentPath = FilenameUtils.getFullPath(name);
            // basename = FilenameUtils.getBaseName(name);
            // extension = "." + FilenameUtils.getExtension(name);
            Path parentPath = templatePath.get().getParent();
            basename = FilenameUtils.getBaseName(templatePath.get().toString());
            extension = "." + FilenameUtils.getExtension(templatePath.get().toString());

            File parentFile = parentPath.toFile();

            // templateLoader = new FileTemplateLoader(parentPath, extension);
            templateLoader = new FileTemplateLoader(parentFile, extension);
        } else {
            extension = ".md";
            basename = "default_toc.md";
            templateLoader = new ClassPathTemplateLoader("resources", extension);
        }

        Handlebars handlebars = new Handlebars(templateLoader);

        Path outputPath = docsPath.resolve(TOC_FILE_NAME + extension);

        try {
            Template template = handlebars.compile(basename);
            String tocContents = template.apply(this);

            Files.writeString(outputPath, tocContents,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);

        } catch (IOException e) {
            String msg = "ERROR: Could not generate table of contents " + outputPath.toString() + " from template "
                    + basename;
            throw new ADRException(msg,
                    e);
        }

        return outputPath;
    }

}

// * [ADR {{{id}}}]({{{adr.filename}}}) : {{{adr.title}}}
class TocEntry {

    private final int id;
    private final String filename;
    private final String title;

    TocEntry(int id, String filename, String title) {
        this.id = id;
        this.filename = filename;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public String getTitle() {
        return title;
    }

    public void format() {
        // Stub TODO
        String displayedTitle = StringUtils.capitalize(title);
        System.out.println("[ADR " + id + "]" + "(" + filename + ") : " + displayedTitle);
    }
}
