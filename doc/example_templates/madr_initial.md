# Record Architecture Decisions

* Status: accepted

* Date: {{date}}

## Context and Problem Statement

Decisions that might have an impact on the architecture are architectural decisions. It should be as easy as possible to:

 1. Write down the decisions
 2. Version the decisions

## Decision Drivers

* Architecture decisions should be short and to the point.
* The architecture decision should be versionable in command software development tools such as [Git](https://git-scm.com/).
* The administration of the architecure decisions (such as nummering, dating, links) should be tool supported.

## Considered Options

* Collect the architecture decisions in a single document.
* Collect each architecture decision in a separate file that can be versioned, i.e. as a lightweight Architecture Decision Record (ADR) and manually administer it.
* Use a tool to support the creation and administration of ADRs.

## Decision Outcome

Chosen option: "Collect each architecture decision in a separate file".

1. Use the [MADR](https://adr.github.io/madr/) format to document the architecture decisions succinctly
2. Write each architecture decision in a separate file that can be versioned, i.e. as a lightweight Architecture Decision Record (ADR)
3. Use the  [adr-j](https://github.com/adoble/adr-j) tool to write and administer lightweight ADRs
