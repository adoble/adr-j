# 5. Help comments

Date: 21.07.2017

## Status

Accepted



## Context

The tool will have a help subcommand to provide documentation for users.

It is usful to have the usage documentation in the code. When reading the code, that's the first place to look for information about how to run the command.

## Decision

The command classes are annotated with usage documentation. This is actively read when usage documentation needs to be displayed, thus avoiding the use of separate help files etc..

## Consequences

No need to maintain help text in a separate file.

Help text can easily be kept up to date as the code is edited.

There's no automated check that the help text is up to date. The tests do not work well as documentation for users, and the help text is not easily cross-checked against the code.
