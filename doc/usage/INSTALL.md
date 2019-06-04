# Install ADR-J

## Windows

1. Download the source code.
2. Make sure you have gradle installed (https://gradle.org/).
3. Run `gradlew releaseJar`. This should create a file `build/releases/adr-j.jar`.
4. Set the environment variable `ADR_HOME` to the folder where you downloaded the source code. This should contain the `build` folder.
5. Set the environment variable `EDITOR` or `VISUAL` to the location of the editor you what to use for editing the ADRs (e.g. Atom)
6. Add `%ADR_HOME%\launch-scripts` to the `PATH` environment variable

You should now be able to type `adr` from the command line and see a response.

## Unix

1. Download the source code.
2. Make sure you have gradle installed (https://gradle.org/).
3. Run `.\gradlew releaseJar`. This should create a file `build/releases/adr-j.jar`.
4. Set the environment variable `ADR_HOME` to the folder where you downloaded the source code. This should contain the `build` folder. For instance this could be done by using the adding the following to the `~/.bashrc` file:
```
# For example
export ADR_HOME=~/adr-j
```

5. Set the environment variable `EDITOR` or `VISUAL` to the location of the editor you what to use for editing the ADRs (e.g. Atom), e.g. in the `~/.bashrc` file:
```
# For example
export EDITOR=/usr/bin/vi
```

6. Move `%ADR_HOME%\launch-scripts\adr` to the `~/bin` directory.

Of course, there are many other ways to install adr-j on unix depending on your personal preferences; important is that the environment variables are set.
