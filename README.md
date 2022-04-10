
# ADR-J Tool
![Release](https://img.shields.io/github/v/release/adoble/adr-j)
![Build Status](https://github.com/adoble/adr-j/actions/workflows/gradle.yml/badge.svg)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)


A Java based command-line tool for working with [Architecture Decision Records][ADRs] (ADRs). More information about what ADRs are and why to use them can be found in this [article](https://adr.github.io/).

ADR-J is based on the [script based tools from Nat Pryce](https://github.com/npryce/adr-tools). It has been extended so that:

- Custom ADR templates can be created that fit into in-house development guidelines.

- Different markup formats can be used in the templates (for instance **Markdown** and **AsciiDoc**) meaning that the ADRs can be used with different in-house documentation tools.  

# Quick Start

[Install ADR-J tool](./doc/usage/INSTALL.md)

Use the `adr` command to manage ADRs.  Try running `adr help`.

ADRs are stored in your project as markup files in the `doc/adr` directory.


1. Create an ADR directory in the root of your project:

        adr init 

    This will create the first ADR recording that you are using ADRs
    to record architectural decisions and linking to
    [Michael Nygard's article on the subject][ADRs]. The default is to use the Nygard template, GitHub markdown and the relative directory `doc/adr`. 
 
    To use a different template to the default, specify the path of a template:

       adr init -t ~/standards/madr.md 

    A guide to writing templates can be found [here](./doc/usage/Writing_Templates.md). Example templates can be found [here](./doc/example_templates).

    To use a different directory for the created ADRs specify the directory:

       adr init doc/architecture/decisions

    To combine these both use:

        adr init -t ~/standards/madr.md doc/architecture/decisions


2. Create Architecture Decision Records

        adr new Implement as Unix shell scripts

    This will create a new, numbered ADR file and open it in your
    editor of choice (as specified by the ADR_EDITOR environment
    variable).

    To create a new ADR that supersedes a previous one (ADR 9, for example), use the -s option.

        adr new -s 9 Use Rust for performance-critical functionality

    This will create a new ADR file that is flagged as superseding
    ADR 9.  It then opens the new ADR in your
    editor of choice.

    To create a new ADR that references another ADR, use the -l option.

       adr new -l 4:"Links to" Use JMS interface for messaging

   This will create a new ADR that references ADR 4 and inserts the message
   "Links to" in the new ADR.  

3. For further information, see the [man pages](doc/man/adr.md) or use the built in help:

        adr help



# Architecture Decisions

The decisions for this tool are recorded as [architecture decision records in the project repository](doc/adr/).

# Build


This project uses Gradle to build. Execute following command to generate `build/release/adr-j.jar`

    gradlew releaseJar

[ADRs]: http://thinkrelevance.com/blog/2011/11/15/documenting-architecture-decisions
