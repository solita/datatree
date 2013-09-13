// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree.xml;

import fi.solita.datatree.Tree;
import org.junit.*;
import org.junit.rules.ExpectedException;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import static fi.solita.datatree.Tree.tree;
import static fi.solita.datatree.xml.XmlSchema.*;

public class XmlSchemaValidatorTest {

    private static final Tree schema = schema(element("foo"));
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
    public void validates_Source_schema_and_Tree_subject() {
        XmlSchemaValidator.validate(asSource(schema), validTree);
        thrown.expect(ValidationException.class);
        XmlSchemaValidator.validate(asSource(schema), invalidTree);
    }

    @Test
    public void validates_Tree_schema_and_Source_subject() {
        XmlSchemaValidator.validate(schema, asSource(validTree));
        thrown.expect(ValidationException.class);
        XmlSchemaValidator.validate(schema, asSource(invalidTree));
    }

    @Test
    public void validates_Source_schema_and_Source_subject() {
        XmlSchemaValidator.validate(asSource(schema), asSource(validTree));
        thrown.expect(ValidationException.class);
        XmlSchemaValidator.validate(asSource(schema), asSource(invalidTree));
    }

    private static Source asSource(Tree tree) {
        return new DOMSource(XmlDocumentGenerator.toNamespaceAwareDocument(tree));
    }
}
