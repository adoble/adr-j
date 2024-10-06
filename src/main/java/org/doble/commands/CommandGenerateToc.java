package org.doble.commands;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.doble.adr.ADR;
import org.doble.adr.ADRFilter;
import org.doble.adr.ADRProperties;
import org.doble.adr.Environment;
import org.doble.commands.CommandADR;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import org.apache.commons.lang3.StringUtils;

@Command(name = "toc", description = "Generate a table of contents (TOC) in the same directory as the ADRs.")
public class CommandGenerateToc implements Callable<Integer> {

    @ParentCommand
    CommandGenerate commandGenerate;

    private Environment env;

    ADRProperties properties;

    @Override
    public Integer call() throws Exception {
        List<String> adr_file_names;
        List<TocEntry> tocEntries = new ArrayList<>();

        env = commandGenerate.commandADR.getEnvironment();

        // Determine where the .adr directory is stored, i.e. the root path.
        // If the directory has not been initialized, this will throw an exception
        Path rootPath = ADR.getRootPath(env);

        // Load the properties
        properties = new ADRProperties(env);
        properties.load();

        // Determine where the ADRs are stored
        Path docsPath = rootPath.resolve(properties.getProperty("docPath"));

        adr_file_names = this.getADRFileNames(docsPath);

        // Pattern to match and capture the the parts of the adr filename
        Pattern pattern = Pattern.compile("^(\\d{4})-([a-zA-Z0-9.-]+)(?:\\.[a-z]+)$");

        System.out.println("Toc stub:");
        for (String adr_file_name : adr_file_names) {

            Matcher matcher = pattern.matcher(adr_file_name);
            if (matcher.matches()) {
                TocEntry tocEntry = new TocEntry();
                tocEntry.id = matcher.group(1); // First capture group (the number)
                tocEntry.title = matcher.group(2).replace("-", " "); // Second group, replace dashes with spaces
                tocEntry.filename = adr_file_name;
                tocEntries.add(tocEntry);

            }
        }

        tocEntries.forEach(TocEntry::format);

        String tocFileName = "toc.md";
        Path tocPath = docsPath.resolve(tocFileName);

        writeToc(tocPath, tocEntries);

        // adr_file_names.forEach(System.out::println);
        return 0;

    }

    public List<String> getADRFileNames(Path directoryPath) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath)) {
            return StreamSupport.stream(stream.spliterator(), false)
                    .filter(Files::isRegularFile)
                    .filter(ADRFilter.filter()) // Only valid ADRs in the building
                    .sorted()
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Error reading the directory: " + e.getMessage());
            return List.of();
        }
    }

    // TODO

    void writeToc(Path tocPath, List<TocEntry> tocEntries) {

    }

}

// * [ADR {{{id}}}]({{{adr.filename}}}) : {{{adr.title}}}
class TocEntry {
    String id;
    String filename;
    String title;

    public void format() {
        // Stub TODO
        String displayedTitle = StringUtils.capitalize(title);
        System.out.println("[ADR " + id + "]" + "(" + filename + ") : " + displayedTitle);
    }
}
