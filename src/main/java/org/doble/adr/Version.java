/**
  A central place to maintain the version number of adr-j.
*/

/* Currently only use a simple string as there is no functionality associated with
   the version number. This may change if:
   a. The version number is taken from, for instance, a build script.  
   b. Adherence to the semantic versioning rules need to be enforced, for instance, with 
      [SemVer](https://github.com/vdurmont/semver4j/tree/master)
 */
package org.doble.adr;

import picocli.CommandLine.IVersionProvider;

public final class Version implements IVersionProvider {
  /**********************************************************
   * VERSION NUMBER
   * 
   * Version numbers adhere to to Semantic Versioning:
   * https://semver.org/spec/v2.0.0.html
   *
   ***********************************************************/
  private final static String version = "3.4.0-alpha"; // New command, backwards compatable

  @Override
  public String[] getVersion() throws Exception {

    return new String[] { version };
  }

}
