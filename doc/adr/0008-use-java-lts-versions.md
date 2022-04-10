# 8. Use Java LTS Versions

Date: 10 Apr 2022

## Status

Accepted

* Supersedes [ADR 2](0002-implement-as-Java.md)

## Context

ADRs need to be created on a wide variety of operating systems.

## Decision

The tool is written in Java and uses the operating system independence of the Java platform.

In order to profit from new features in the Java langauge the tool is compiled using the last LTS version of Java. 

## Consequences

As Java is not a scripting language, the code will be more complex.

The code base has to be constantly maintained to keep up with the new LTS versions.