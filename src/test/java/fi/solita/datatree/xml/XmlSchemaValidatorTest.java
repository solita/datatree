// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree.xml;

import fi.solita.datatree.Tree;
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
        XmlSchemaValidator.validate(schema, validTree);
        thrown.expect(ValidationException.class);
        XmlSchemaValidator.validate(schema, invalidTree);
    }

    @Test
    public void validates_Tree_schema_and_Source_subject() {
        XmlSchemaValidator.validate(schema, asSource(validTree));
        thrown.expect(ValidationException.class);
        XmlSchemaValidator.validate(schema, asSource(invalidTree));
    }

    @Test
    public void validates_URL_schema_and_Tree_subject() {
        XmlSchemaValidator.validate(schemaUrl, validTree);
        thrown.expect(ValidationException.class);
        XmlSchemaValidator.validate(schemaUrl, invalidTree);
    }

    @Test
    public void validates_URL_schema_and_Source_subject() {
        XmlSchemaValidator.validate(schemaUrl, asSource(validTree));
        thrown.expect(ValidationException.class);
        XmlSchemaValidator.validate(schemaUrl, asSource(invalidTree));
    }

    @Test
    public void validates_Source_schema_and_Tree_subject() {
        XmlSchemaValidator.validate(asSource(schema), validTree);
        thrown.expect(ValidationException.class);
        XmlSchemaValidator.validate(asSource(schema), invalidTree);
    }

    @Test
    public void validates_Source_schema_and_Source_subject() {
        XmlSchemaValidator.validate(asSource(schema), asSource(validTree));
        thrown.expect(ValidationException.class);
        XmlSchemaValidator.validate(asSource(schema), asSource(invalidTree));
    }

    @Test
    public void validating_a_schema_will_not_download_any_files_from_the_internet() throws Exception {
        UrlSpy.openedConnections.clear();

        XmlSchemaValidator.validate(XmlSchema.XSD, schema(element("foo")));

        removeIfProtocol("file", UrlSpy.openedConnections);
        assertThat("downloaded files", UrlSpy.openedConnections, is(empty()));
    }

    private static void removeIfProtocol(String protocol, List<URL> urls) {
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
