# Usage

`adr new [-l=<links>]... [-s=<supersedes>]... TITLETEXT...`

# Description

Creates a new, numbered ADR.  The `<title_text>` arguments are concatenated to form the title of the new ADR. The ADR is opened for editing in the editor specified by the `VISUAL` or `EDITOR` environment variable (`VISUAL` is preferred;
`EDITOR` is used if `VISUAL` is not set).  After editing, the file name of the ADR is output to stdout, so the command can be used in scripts.

# Parameters

`TITLETEXT...`    The TITLETEXT arguments are concatenated to form the title of the new ADR.

`-l, -link=<links>`   Links the new ADR to a previous ADR.  A specification of  a link to another ADR is in the form `<target_adr>:<link_description>:<reverse_link_description>`

* `<target_adr>` is a reference (number or partial filename) of a previous decision.

* `<link_description>` is the description of the link created in the new ADR.

Multiple `-l` options can be given, so that the new ADR can link to multiple existing ADRs

`-s, supersedes=<supersedes>` A reference (number) of a previous decision that the new decision supersedes. A markdown to the superseded ADR is inserted into the Status section.	The status of the superseded ADR is changed to record that it has been superseded by the new ADR. Multiple `-s` options can be given, so that the new ADR can supersede multiple existing ADRs
