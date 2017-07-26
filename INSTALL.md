# Install ADR-J

## Windows

1. Download the source code
2. Run `mvn install`
3. Set the environment variable `ADR_HOME` to the the directory containing the target  
4. Set the environment variable `EDITOR` or `VISUAL` to the location of the editor you what to use for editing the ADRs (e.g. Atom)
5. Add `...\launch-scripts\adr.bat` to the `PATH` environment variable

You should now be able to type `adr` from the command line and see a response.
