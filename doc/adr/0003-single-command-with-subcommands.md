# 3. Single command with subcommands

Date: 21.07.2017

## Status

Accepted



## Context

The tool provides a number of related commands to create and manipulate architecture decision records.

How can the user find out about the commands that are available?

## Decision

The tool defines a single command, called adr.

The first argument to adr (the subcommand) specifies the action to perform. Further arguments are interpreted by the subcommand.

Running adr without any arguments lists the available subcommands.

Subcommands are implemented as Java classes with a defined interface in the package `adr.commmand`. A reflection mechanism is used so that new commands can be added without changing the rest of the code e.g. the subcommand new is implemented as class`CommandNew`, the subcommand help as the class `CommandHelp` and so on. Each command class is annotated with the name of the command (so the class name is not important) and also with its own help instructions so that it is completely self-contained.


## Consequences

Users can more easily explore the capabilities of the tool.

Users are already used to this style of command-line tool. For example, Git works this way.

Each subcommand can be implemented by different developers.
