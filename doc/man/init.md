# Usage

`adr init [-i=INITIALTEMPLATE] [-t=TEMPLATE] [DOCDIR]`

# Description

Initialise the directory of architecture decision records:
* creates a subdirectory of the current working directory
* creates the first ADR in that subdirectory, recording the decision to record
architectural decisions with ADRs.

`[DOCDIR]`             The directory to store the ADRs relative to  the current directory. Default is `doc/adr`.

`-i, -initial=INITIALTEMPLATE` A template for the initial ADR created during intialization.

`-t, -template=TEMPLATE`   Template file used for ADRs.
