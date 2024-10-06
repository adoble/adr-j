WARNING: Command not yet implemented!

# Usage

`adr generate toc [-t TEMPLATE_FILE]`

# Description

Generate a table of contents (TOC) listing each ADR. The table of contents is generated in the same directory as the ADRs.

`-t, -template TEMPLATE_FILE`   Optional template file used for the TOC. If no template is specified then:

* The template specified with `adr config tocTempateFile [path to template]` is used.
* If no template has been configured then a default tempplate using markdown is generated.