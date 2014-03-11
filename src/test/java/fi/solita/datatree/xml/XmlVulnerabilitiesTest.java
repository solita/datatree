// Copyright © 2013-2014 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree.xml;

import org.apache.commons.io.IOUtils;
import org.junit.*;
import org.junit.rules.ExpectedException;

public class XmlVulnerabilitiesTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void safe_XML_passes_the_check() {
        String xml = "<?xml version=\"1.0\"?>" +
                "<foo></foo>";

        XmlVulnerabilities.check(IOUtils.toInputStream(xml));
    }

    @Test
    public void notices_XXE_attacks() {
        // See https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Processing
        String xml = "<?xml version=\"1.0\"?>" +
                "<!DOCTYPE foo [" +
                "  <!ELEMENT foo ANY >" +
                "  <!ENTITY xxe SYSTEM \"file:///secret.txt\" >" +
                "]>" +
                "<foo>&xxe;</foo>";

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("DOCTYPE is disallowed");
        XmlVulnerabilities.check(IOUtils.toInputStream(xml));
    }

    @Test
    public void notices_XML_bombs() {
        // See http://msdn.microsoft.com/en-us/magazine/ee335713.aspx
        String xml = "<?xml version=\"1.0\"?>" +
                "<!DOCTYPE foo [" +
                "  <!ENTITY a \"aaaaaaaaaaaaaaaaaa\">" +
                "]>" +
                "<foo>&a;&a;&a;&a;&a;&a;&a;&a;&a;</foo>";

        // this would use gigabytes of memory:
//        attacker = "<?xml version=\"1.0\"?>\n" +
//                "<!DOCTYPE lolz [\n" +
//                "  <!ENTITY lol \"lol\">\n" +
//                "  <!ENTITY lol2 \"&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;\">\n" +
//                "  <!ENTITY lol3 \"&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;\">\n" +
//                "  <!ENTITY lol4 \"&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;\">\n" +
//                "  <!ENTITY lol5 \"&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;\">\n" +
//                "  <!ENTITY lol6 \"&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;\">\n" +
//                "  <!ENTITY lol7 \"&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;\">\n" +
//                "  <!ENTITY lol8 \"&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;\">\n" +
//                "  <!ENTITY lol9 \"&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;\">\n" +
//                "]>\n" +
//                "<lolz>&lol9;</lolz>";

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("DOCTYPE is disallowed");
        XmlVulnerabilities.check(IOUtils.toInputStream(xml));
    }
}
