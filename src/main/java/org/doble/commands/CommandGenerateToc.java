package org.doble.commands;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.doble.adr.ADR;
import org.doble.adr.ADRProperties;
import org.doble.adr.Environment;
import org.doble.commands.CommandADR;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

@Command(name = "toc", description = "Generate a table of contents (TOC) in the same directory as the ADRs.")
public class CommandGenerateToc implements Callable<Integer> {

    @ParentCommand
    CommandGenerate commandGenerate;

    private Environment env;

    ADRProperties properties;

    @Override
    public Integer call() throws Exception {
        List<String> adr_file_names;

        // CommandADR commandAdr = commandGenerate.commandADR;
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

        System.out.println("Toc stub:");
        adr_file_names.forEach(System.out::println);
        return 0;
    }

    public List<String> getADRFileNames(Path directoryPath) {
        List<String> fileNames = new ArrayList<>();

        if (Files.isDirectory(directoryPath)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath)) {
                for (Path path : stream) {
                    if (Files.isRegularFile(path)) {
                        fileNames.add(path.getFileName().toString());
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading the directory: " + e.getMessage());
            }
        } else {
            System.out.println("Provided path is not a directory.");
        }

        return fileNames;
    }

}
