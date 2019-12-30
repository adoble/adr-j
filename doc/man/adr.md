Usage: adr [COMMAND]
Creation and management of architectural decision records (ADRs)
Commands:
  init     Initialise the directory of architecture decision records:
            * creates a subdirectory of the current working directory
            * creates the first ADR in that subdirectory, recording the
             decision to record architectural decisions with ADRs.
  new      Creates a new, numbered ADR.  The <title_text> arguments are
             concatenated to form the title of the new ADR. The ADR is opened
             for editing in the editor specified by the VISUAL or EDITOR
             environment variable (VISUAL is preferred; EDITOR is used if
             VISUAL is not set).  After editing, the file name of the ADR is
             output to stdout, so the command can be used in scripts.
  list     Lists the filenames of the currently created architecture decision
             records.
  version  Prints the version of adr-j.
  edit     Starts the editor on the specified ADR
  config   List of the currently set properties.
  help     Displays help information about the specified command
Exit Codes:
   0   Successful program execution.
  64   Invalid input: an unknown option or invalid parameter was specified.
  70   Execution exception: an exception occurred while executing the business
         logic.
Environment Variables:
  ADR_AUTHOR   The author of the ADR
