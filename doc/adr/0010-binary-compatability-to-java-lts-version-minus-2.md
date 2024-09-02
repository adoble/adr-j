# 10. Binary compatability to Java LTS version minus 2

Date: 2 Sept 2024

## Status

Proposed

* Extends [ADR 8](0008-use-java-lts-versions.md)


## Context

[ADR 8](0008-use-java-lts-versions.md) specifies that `adr-j` is always developed using the latest Java LTS version. However, many organisations are not using the latest LTS version of Java. 

Binary compatability with earlier versions could be provided, but this is taken too far it can be restrictive in the further development of `adr-j`. 

## Decision

We will provide binary compatablity with the two previous LTS versions of Java. 

## Consequences

* Relatively new Java features can be used in the development.  
* The lowest supported version is always deprecated. 
* Versions before the deprecated version are not supported. 


For instance:

| Development | Binary compatability | Deprecated | Not supported      |
| ----------- | -------------------- | ---------- | ------------------ |
| Java 21     | Java 17, Java 11     | Java 11    | Java 8 and before  |
| Java 25 (*planned 3Q25*) | Java 21, Java 17     | Java 17    | Java 11 and before |

