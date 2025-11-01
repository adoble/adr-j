# Install ADR-J

## Installation using JBang

The easiest way to install is to use [JBang](https://www.jbang.dev/).

1. If not already installed, then [install JBang](https://www.jbang.dev/download/) .
2. Run 

    ```
    jbang app install adr@adoble
    ```

    If a version of `adr-j` has already been installed with `jbang` then use the following to overwrite it:

    ```
    jbang app install --force adr@adoble
    ```

3. Set up the [environment variables](#setting-up-environment-variables) depending on your OS.

4. Run `adr`, e.g.:

    ```
    adr version
    ```

## Downloading the JAR

1. Download the JAR from the **Releases** section.
2. Set up the [environment variables](#setting-up-environment-variables) depending on your OS.
3. Run: 

    ```
    java -jar {path to downloaded jar file}/adr-j.jar
    ```

    It is recommended to setup a script so that the above can be run as `adr`.

## Installation from Code

### Windows

1. Download the source code.
2. Make sure you have [Java version 21](https://jdk.java.net/21/) or higher installed.
3. Make sure you have Gradle installed (https://gradle.org/).
4. Run `gradlew releaseJar`. This should create a file `build\releases\adr-j.jar`.
5. Set up the [environment variables](#setting-up-environment-variables) depending on you OS.

6. Either
   - Add {project directory}\launch-scripts` to the `PATH` environment variable. 
   - Or install using JBang (see above) using:
       ```
      jbang app install {project directory}\build\releases\adr-j.jar 
      ```

You should now be able to type `adr` from the command line and see a response.

### Unix

1. Download the source code.
2. Make sure you have gradle installed (https://gradle.org/).
3. Run `.\gradlew releaseJar`. This should create a file `build/releases/adr-j.jar`.
4. Set up the [environment variables](#setting-up-environment-variables) depending on you OS.

5. Either
   - Move `{project directory}/launch-scripts/adr` to the `~/bin` directory.
   - Or install using JBang (see above) using:
       ```
      jbang app install {project directory}/build/releases/adr-j.jar 
      ```

You should now be able to type `adr` from the command line and see a response.

Of course, there are many other ways to install adr-j on unix depending on your personal preferences; important is that the environment variables are set.


# Setting up Environment Variables

### Windows

Set the environment variable `ADR_EDITOR` or `ADR_VISUAL` to the location of the editor you what to use for editing the ADRs (e.g. VSCode). If none of those variables are set, ADR will use `EDITOR` and `VISUAL` variables. If the path to the editor has spaces in it, do **not** use quotes in the enviroment variable, e.g.  instead of `...\Programs\"Microsoft VS Code"\bin\code.cmd` use instead `...\Programs\Microsoft VS Code\bin\code.cmd`.



### Unix

Set the environment variable `EDITOR` or `VISUAL` to the location of the editor you what to use for editing the ADRs (e.g. VSCode), e.g. in the `~/.bashrc` file:

    ```
    # For example
    export EDITOR=/usr/bin/vi
    ```
