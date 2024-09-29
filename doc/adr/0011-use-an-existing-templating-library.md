# 11. Use an existing templating library

Date: 29 Sept 2024

## Status

Proposed


## Context

Much of the adr-j code is custom code to deal with templates and the fields that they contain.  This custom code needs to be extended in many locations to 
handle changes such as introducing new fields (see, for instance, [issue 49(https://github.com/adoble/adr-j/issues/49)]). If new types of generated content is required, 
for instance, a table of contents, then much of this code needs to be duplicated.  


## Decision

Reduce the templating code and minimise code duplication by using an existing templating library. 

The templating library to be used is the [Handlebars library](https://github.com/jknack/handlebars.java). Reasons for this are: 
- Templating variables (known as fields in `adr-j`) use a similar notation (double braces) to those currently used in `adr-j` templates, minimising the amount of work in migrating existing templates. 
- The templates are logic-less and thus fit into the way tmeplates are currently constructed.
- The template construction is amenable to extracting field values from generated content and thus do not block the way for future development of `adr-j`.
- The templating can be made more powerful (e.g. user defined handling of empty lists).
- Works with any type of content:  markdown, asciidoc etc.

## Consequences

For existing projects, the templates would need to be changed. This could cause significant work in migrating existing projects.  Therefore the following is proposed:  
  - `handlebars` templating is only used for new types of content (e.g. table of contents).
  - `handlebars` templating for all for ADRs will only be introduced in a major new release of `adr-j` which can be coupled with signifcant improvements in functionality making any changes worthwhile. 
   
