package org.doble.commands;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import org.doble.adr.ADR;
import org.doble.adr.ADRException;
import org.doble.adr.ADRFilter;
import org.doble.adr.ADRProperties;
import org.doble.adr.DateFormatEnum;
import org.doble.adr.Environment;
import org.doble.adr.model.TableOfContents;

@Command(name = "toc", description = "Generate a table of contents (TOC) in the same directory as the ADRs.")
public class CommandGenerateToc implements Callable<Integer> {

    private static final String DEFAULT_TOC_TEMPLATE = "default_toc_template.md";

    @Option(names = { "-t", "-template" }, description = "The template used to crate the table of contents.")
    Optional<String> templatePathName;;

    @ParentCommand
    CommandGenerate commandGenerate;

    private Environment env;

    ADRProperties properties;

    @Override
    public Integer call() throws ADRException {
        env = commandGenerate.commandADR.getEnvironment();

        // Load the properties
        properties = new ADRProperties(env);
        properties.load();

        // Determine where the .adr directory is stored, i.e. the root path.
        // If the directory has not been initialized, this will throw an exception
        Path rootPath = ADR.getRootPath(env);

        // Load the properties
        properties = new ADRProperties(env);
        properties.load();

        // Determine where the ADRs are stored
        Path docsPath = rootPath.resolve(properties.getProperty("docPath"));

        DateTimeFormatter dateFormatter = determineDateFormatter();

        TableOfContents toc = buildTableOfContents(docsPath, dateFormatter);

        Optional<Path> templatePath = determineTemplatePath(properties);

        toc.createPersistentRepresentation(templatePath);

        return 0;

    }

    private DateTimeFormatter determineDateFormatter() throws ADRException {
        // Determine the date formatter
        String dateFormat = properties.getProperty("dateFormat");
        DateTimeFormatter dateFormatter;
        try {
            DateFormatEnum dateFormatEnum = DateFormatEnum.valueOf(dateFormat);
            dateFormatter = dateFormatEnum.getDateTimeFormatter();
        } catch (IllegalArgumentException e) {
            throw new ADRException("ERROR: Can not parse dateFormat \'" + dateFormat + "\'");
        }
        return dateFormatter;
    }

    private TableOfContents buildTableOfContents(Path docsPath, DateTimeFormatter dateFormatter) {
        List<String> adr_file_names;
        TableOfContents toc = new TableOfContents(docsPath, dateFormatter);

        adr_file_names = this.getADRFileNames(docsPath);

        // Pattern to match and capture the the parts of the adr filename
        Pattern pattern = Pattern.compile("^(\\d{4})-([a-zA-Z0-9.-]+)(?:\\.[a-z]+)$");

        for (String adr_file_name : adr_file_names) {
            Matcher matcher = pattern.matcher(adr_file_name);
            if (matcher.matches()) {
                int id = Integer.parseInt(matcher.group(1)); // First capture group (the number)
                String title = matcher.group(2).replace("-", " "); // Second group, replace dashes with spaces
                String filename = adr_file_name;
                toc.addEntry(id, filename, title);
            }
        }
        return toc;
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

    private Optional<Path> determineTemplatePath(ADRProperties properties) throws ADRException {

        Optional<Path> templatePath = this.templatePathName
                .map(pathName -> env.fileSystem.getPath(pathName))
                .or(() -> {
                    Optional<String> propTemplatePathName = Optional
                            .ofNullable(properties.getProperty("tocTemplateFile"));
                    return propTemplatePathName.map(env.fileSystem::getPath);
                });

        if (templatePath.isPresent()) {
            if (!Files.exists(templatePath.get())) {
                throw new ADRException("ERROR: The template file \'" + templatePathName + " does not exist.");

            }
        }

        return templatePath;

    }

    // /**
    // * Returns the path to the default TOC template.
    // * This works in IDEs, Gradle, standalone programs, or executable JARs.
    // *
    // * @return An optional Path to the resource. Empty if it cannot be found and
    // an
    // * message is sent to the user.
    // */
    // private Optional<Path> getDefaultFromResourcePath() {
    // // Use the class loader to load the resource
    // var resourceURL =
    // CommandGenerateToc.class.getClassLoader().getResource(DEFAULT_TOC_TEMPLATE);

    // if (resourceURL == null) {
    // env.err.println("FATAL ERROR: Invalid syntax for the default TOC template: "
    // + DEFAULT_TOC_TEMPLATE);
    // return Optional.empty();
    // }

    // try {
    // // Convert the resource URL to a Path object
    // return Optional.of(Paths.get(Objects.requireNonNull(resourceURL).toURI()));
    // } catch (URISyntaxException e) {
    // env.err.println("Invalid URI syntax for resource: " + DEFAULT_TOC_TEMPLATE);
    // return Optional.empty();
    // }
    // }

}
