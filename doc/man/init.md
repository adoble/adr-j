# Usage

`adr init [-i=INITIALTEMPLATE] [-t=TEMPLATE] [DOCDIR]`

# Description

Initialise the directory of architecture decision records:
* creates a subdirectory of the current working directory
* creates the first ADR in that subdirectory, recording the decision to record
architectural decisions with ADRs.

`[DOCDIR]`             The directory to store the ADRs relative to the current directory. Default is `doc/adr`.

`-i, -initial=INITIALTEMPLATE` A template for the initial ADR created during intialization. This can be specified as either an absolute path or a path relative to the project root directory (where the `.adr` directory is located). If an initial template file is specified then a template also has to be specified (with the `-template` option).

`-t, -template=TEMPLATE`   Template file used for ADRs.
