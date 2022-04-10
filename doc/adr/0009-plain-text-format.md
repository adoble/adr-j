# 9. Plain text format

Date: 10 Apr 2022

## Status

Accepted

* Supersedes [ADR 4](0004-markdown-format.md)

## Context

The decision records must be stored in a plain text format:

- This works well with version control systems.

- It allows the tool to modify the status of records and insert hyperlinks when one decision supersedes another.

- Decisions can be read in the terminal, IDE, version control browser, etc.

People will want to use some formatting: lists, code examples, and so on.

People will want to view the decision records in a more readable format than plain text, and maybe print them out.

## Decision

Record architecture decisions in a markup format that is simple to write. Examples of these are [Markdown](https://github.github.com/gfm/) and [AsciiDoc](https://asciidoctor.org/docs/asciidoc-writers-guide/) format.


## Consequences

Decisions can be read in the terminal or in a wide variety of editors.

Decisions will be formatted nicely and hyperlinked by project hosting sites such as GitHub and Bitbucket.

Placing semantic information in the templates becomes more difficult as these should be kept as comments (so that they do not become visible to readers of the ADRs), but comments are handled differently by different markup lanaguages.

