package org.doble.commands;

import java.util.concurrent.Callable;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

@Command(name = "toc", description = "Generate a table of contents (TOC) in the same directory as the ADRs.")
public class CommandGenerateToc implements Callable<Integer> {

    @Override
    public Integer call() throws Exception { // your business logic goes here...
        System.out.println("Toc stub");
        return 0;
    }

}
