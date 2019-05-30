# Install ADR-J

## Windows

1. Download the source code.
2. Make sure you have gradle installed (https://gradle.org/).
3. Run `gradlew releaseJar`. This should create a file `build/releases/adr-j.jar`.
4. Set the environment variable `ADR_HOME` to the folder where you downloaded the source code. This should contain the `build` folder.
5. Set the environment variable `EDITOR` or `VISUAL` to the location of the editor you what to use for editing the ADRs (e.g. Atom)
6. Add `%ADR_HOME%\launch-scripts` to the `PATH` environment variable

You should now be able to type `adr` from the command line and see a response.
