// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree;

import fi.jumi.launcher.JumiBootstrap;

public class JumiTests {

    public static void main(String[] args) throws Exception {
        JumiBootstrap bootstrap = new JumiBootstrap();
        bootstrap.suite
                .addJvmOptions("-ea")
                .setIncludedTestsPattern("glob:fi/solita/datatree/**Test.class");
        bootstrap.runSuite();
    }
}
