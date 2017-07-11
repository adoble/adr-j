package org.doble.adr;

/**
 * Runs all tests. 
 * 
 * 
 */

// TODO use a file mock as in https://github.com/google/jimfs

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)

@SuiteClasses({ org.doble.adr.EnvironmentTest.class, 
	org.doble.adr.CommandHelpTest.class,
	org.doble.adr.CommandInitTest.class,
	org.doble.adr.CommandNewTest.class })

public class AllTests {

}
