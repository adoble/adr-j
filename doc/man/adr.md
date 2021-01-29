# Usage

`adr [COMMAND]`

# Description

 Creation and management of architectural decision records (ADRs)

# Commands

  [`init`](init.md)  Initialise the directory of architecture decision records.

[`new`](new.md) Creates a new, numbered ADR.  

[`list`](list.md) Lists the filenames of the currently created architecture decision records.

[`version`](version.md)    Prints the version of adr-j.

[`edit`](edit.md)       Starts the editor on the specified ADR.

[`config`](config.md)      List of the currently set properties.

[`help`](help.md)       Displays help information about the specified command.

Exit Codes:

   `0`   Successful program execution.

  `64`   Invalid input: an unknown option or invalid parameter was specified.

  `70`   Execution exception: an exception occurred while executing the business logic.

# Environment Variables:
  `ADR_AUTHOR`   The author of the ADR
  `ADR_EDITOR`   The editor to open ADRs
  `ADR_VISUAL`   The editor to open ADRs. Ignored when `ADR_EDITOR` set
