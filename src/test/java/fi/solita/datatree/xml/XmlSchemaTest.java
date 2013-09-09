// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree.xml;

import fi.solita.datatree.Tree;
import org.junit.*;
import org.junit.rules.ExpectedException;

import static fi.solita.datatree.Tree.tree;
import static fi.solita.datatree.xml.XmlSchema.*;

public class XmlSchemaTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void element_name() throws Exception {
        Tree schema = schema(element("correct-name"));

        XmlSchemaValidator.validate(schema, tree("correct-name"));
        thrown.expect(ValidationException.class);
        XmlSchemaValidator.validate(schema, tree("wrong-name"));
    }

    @Test
    public void all_elements_in_any_order() throws Exception {
        Tree schema = schema(
                element("root", complexType(all(
                        element("foo"),
                        element("bar")))));

        XmlSchemaValidator.validate(schema,
                tree("root",
                        tree("foo"),
                        tree("bar")));
        XmlSchemaValidator.validate(schema,
                tree("root",
                        tree("bar"),
                        tree("foo")));
        thrown.expect(ValidationException.class);
        XmlSchemaValidator.validate(schema,
                tree("root",
                        tree("foo")));
    }

    @Test
    public void max_occurrences() throws Exception {
        // `maxOccurs > 1` inside xs:all requires XSD 1.1, which Java doesn't support out-of-the-box,
        // so we must be satisfied with xs:sequence

        Tree schema = schema(
                element("root", complexType(sequence(
                        element("foo", maxOccurs(2))))));

        XmlSchemaValidator.validate(schema,
                tree("root",
                        tree("foo")));
        XmlSchemaValidator.validate(schema,
                tree("root",
                        tree("foo"),
                        tree("foo")));
        thrown.expect(ValidationException.class);
        XmlSchemaValidator.validate(schema,
                tree("root",
                        tree("foo"),
                        tree("foo"),
                        tree("foo")));
    }
}
