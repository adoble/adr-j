# 0006. Use command line processing package

Date: 30.05.2019

## Status

Accepted



## Context

As the ADT tool becomes more complex, the processing of the arguments associated with the subcommands also becomes more complex and requires a programming effort out of proportion to the actual implementation of the functionality.

## Decision

We will use a external package for command line processing.

The "traditional" Apache CLI library is too limited for the processing required (e.g. subcommands).

The decison is to use the [Picocli package](https://picocli.info/) as this fits well with the current architecture of the ADR tool (e.g. subcommands are implemented as seperate classes).

## Consequences

* This requires a major refactoring of the code.

* As the tool evolves, more complex handling of arguments can be added without extra code complexity.
