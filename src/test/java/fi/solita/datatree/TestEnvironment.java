// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree;

import static org.junit.Assume.assumeTrue;

public class TestEnvironment {

    public static final String RUN_SLOW_TESTS = "runSlowTests";

    public static void assumeSlowTestsEnabled() {
        String key = RUN_SLOW_TESTS;
        assumeTrue("To run also this test, set the system property: -D" + key + "=true",
                Boolean.parseBoolean(System.getProperty(key, "false")));
    }
}
