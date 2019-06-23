ADR Tools
=========

A Java based command-line tool for working with [Architecture Decision Records][ADRs] (ADRs).

Based on the [script based tools from Nat Pryce](https://github.com/npryce/adr-tools)

Quick Start
-----------
[Install ADR-J tool](./doc/usage/INSTALL.md)

Use the `adr` command to manage ADRs.  Try running `adr help`.

ADRs are stored in your project as Markdown files in the `doc/adr` directory.


1. Create an ADR directory in the root of your project:

        adr init doc/architecture/decisions

    This will create the first ADR recording that you are using ADRs
    to record architectural decisions and linking to
    [Michael Nygard's article on the subject][ADRs].

    To use a different template to the standard, specify the path of a template:

       adr init -t ~/standards/madr.md doc/architecture/decisions

    A guide to writing templates can be found [here](./doc/usage/Writing_Templates.md).

2. Create Architecture Decision Records

        adr new Implement as Unix shell scripts

    This will create a new, numbered ADR file and open it in your
    editor of choice (as specified by the VISUAL or EDITOR environment
    variable).

    To create a new ADR that supersedes a previous one (ADR 9, for example),
    use the -s option.

        adr new -s 9 Use Rust for performance-critical functionality

    This will create a new ADR file that is flagged as superseding
    ADR 9.  It then opens the new ADR in your
    editor of choice.

    To create a new ADR that references another ADR, use the -l option.

       adr new -l 4:"Links to" Use JMS interface for messaging

   This will create a new ADR that references ADR 4 and inserts the message
   "Links to" in the new ADR.  

3. For further information, use the built in help:

        adr help

The decisions for this tool are recorded as [architecture decision records in the project repository](doc/adr/).

Compile
-------

This project uses Gradle for compilation. Execute following command to generate `build/release/adr-j.jar`.

    gradlew releaseJar

[ADRs]: http://thinkrelevance.com/blog/2011/11/15/documenting-architecture-decisions
