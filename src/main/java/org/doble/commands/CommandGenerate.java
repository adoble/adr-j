package org.doble.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

/**
 * Subcommand to generate files based on ADRs
 *
 * @author adoble
 *
 */

@Command(name = "generate", description = "Generate files based on ADRs. Requires a subcommand to specify what is generated.", subcommands = {
        CommandGenerateToc.class
})

public class CommandGenerate {
    @ParentCommand
    CommandADR commandADR;

    public CommandGenerate() {
    }

}
