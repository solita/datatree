// Copyright © 2013-2014 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree.xml;

import fi.solita.datatree.Tree;
import org.apache.commons.io.IOUtils;
import org.junit.*;
import org.junit.rules.ExpectedException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.net.URL;
import java.util.*;

import static fi.solita.datatree.Tree.tree;
import static fi.solita.datatree.xml.XmlSchema.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class XmlSchemaValidatorTest {

    private static final Tree schema = schema(element("foo"));
    private static final URL schemaUrl = XmlSchemaValidatorTest.class.getResource("foo.xsd");
    private static final Tree validTree = tree("foo");
    private static final Tree invalidTree = tree("bar");

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void validates_Tree_schema_and_Tree_subject() {
        new XmlSchemaValidator(schema).validate(validTree);
        thrown.expect(ValidationException.class);
        new XmlSchemaValidator(schema).validate(invalidTree);
    }

    @Test
    public void validates_Tree_schema_and_Source_subject() {
        new XmlSchemaValidator(schema).validate(asSource(validTree));
        thrown.expect(ValidationException.class);
        new XmlSchemaValidator(schema).validate(asSource(invalidTree));
    }

    @Test
    public void validates_URL_schema_and_Tree_subject() {
        new XmlSchemaValidator(schemaUrl).validate(validTree);
        thrown.expect(ValidationException.class);
        new XmlSchemaValidator(schemaUrl).validate(invalidTree);
    }

    @Test
    public void validates_URL_schema_and_Source_subject() {
        new XmlSchemaValidator(schemaUrl).validate(asSource(validTree));
        thrown.expect(ValidationException.class);
        new XmlSchemaValidator(schemaUrl).validate(asSource(invalidTree));
    }

    @Test
    public void validates_Source_schema_and_Tree_subject() {
        new XmlSchemaValidator(asSource(schema)).validate(validTree);
        thrown.expect(ValidationException.class);
        new XmlSchemaValidator(asSource(schema)).validate(invalidTree);
    }

    @Test
    public void validates_Source_schema_and_Source_subject() {
        new XmlSchemaValidator(asSource(schema)).validate(asSource(validTree));
        thrown.expect(ValidationException.class);
        new XmlSchemaValidator(asSource(schema)).validate(asSource(invalidTree));
    }

    /**
     * {@link Source} instances (of the schema) are stateful and cannot be reused,
     * so also our validator is non-reusable.
     */
    @Test
    public void the_validator_object_cannot_be_reused() {
        XmlSchemaValidator validator = new XmlSchemaValidator(schema);
        validator.validate(validTree);

        thrown.expect(IllegalStateException.class);
        validator.validate(validTree);
    }

    @Test
    public void validating_a_schema_will_not_download_any_files_from_the_internet() throws Exception {
        UrlSpy.openedConnections.clear();

        new XmlSchemaValidator(XmlSchema.XSD).validate(schema(element("foo")));

        removeIfProtocolEquals("file", UrlSpy.openedConnections);
        assertThat("downloaded files", UrlSpy.openedConnections, is(empty()));
    }

    @Ignore // TODO
    @Test
    public void is_not_vulnerable_to_XXE_attacks() {
        // See https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Processing
        String attacker = "<?xml version=\"1.0\"?>" +
                "<!DOCTYPE foo [" +
                "  <!ELEMENT foo ANY >" +
                "  <!ENTITY xxe SYSTEM \"file:///secret.txt\" >" +
                "]>" +
                "<foo>&xxe;</foo>";

        thrown.expect(ValidationException.class);
        thrown.expectMessage("DOCTYPE is disallowed");
        new XmlSchemaValidator(schema).validate(new StreamSource(IOUtils.toInputStream(attacker)));
    }

    @Ignore // TODO
    @Test
    public void is_not_vulnerable_to_XML_bombs() {
        // See http://msdn.microsoft.com/en-us/magazine/ee335713.aspx
        String attacker = "<?xml version=\"1.0\"?>" +
                "<!DOCTYPE foo [" +
                "  <!ENTITY a \"aaaaaaaaaaaaaaaaaa\">" +
                "]>" +
                "<foo>&a;&a;&a;&a;&a;&a;&a;&a;&a;</foo>";

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

        thrown.expect(ValidationException.class);
        thrown.expectMessage("DOCTYPE is disallowed");
        new XmlSchemaValidator(schema).validate(new StreamSource(IOUtils.toInputStream(attacker)));
    }


    // helpers

    private static void removeIfProtocolEquals(String protocol, List<URL> urls) {
        for (Iterator<URL> it = urls.iterator(); it.hasNext(); ) {
            URL url = it.next();
            if (url.getProtocol().equals(protocol)) {
                it.remove();
            }
        }
    }

    private static Source asSource(Tree tree) {
        return new StreamSource(XmlGenerator.toInputStream(tree));
    }
}
