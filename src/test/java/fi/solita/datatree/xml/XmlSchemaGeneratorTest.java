// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree.xml;

import fi.solita.datatree.Tree;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.*;
import javax.xml.validation.*;
import java.io.*;

import static fi.solita.datatree.Tree.*;

public class XmlSchemaGeneratorTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void element_name() throws Exception {
        Tree schema = tree("xs:schema", meta("xmlns:xs", "http://www.w3.org/2001/XMLSchema"),
                tree("xs:element", meta("name", "correct-name"),
                        tree("xs:complexType")));

        validate(schema, tree("correct-name"));
        thrown.expect(ValidationException.class);
        validate(schema, tree("wrong-name"));
    }

    @Test
    public void all_elements_in_any_order() throws Exception {
        Tree schema = tree("xs:schema", meta("xmlns:xs", "http://www.w3.org/2001/XMLSchema"),
                tree("xs:element", meta("name", "root"),
                        tree("xs:complexType",
                                tree("xs:all",
                                        tree("xs:element", meta("name", "foo")),
                                        tree("xs:element", meta("name", "bar"))))));

        validate(schema,
                tree("root",
                        tree("foo"),
                        tree("bar")));
        validate(schema,
                tree("root",
                        tree("bar"),
                        tree("foo")));
        thrown.expect(ValidationException.class);
        validate(schema,
                tree("root",
                        tree("foo")));
    }

    @Test
    public void max_occurrences() throws Exception {
        // `maxOccurs > 1` inside xs:all requires XSD 1.1, which Java doesn't support out-of-the-box,
        // so we must be satisfied with xs:sequence

        Tree schema = tree("xs:schema", meta("xmlns:xs", "http://www.w3.org/2001/XMLSchema"),
                tree("xs:element", meta("name", "root"),
                        tree("xs:complexType",
                                tree("xs:sequence",
                                        tree("xs:element",
                                                meta("name", "foo"),
                                                meta("maxOccurs", "2"))))));

        validate(schema,
                tree("root",
                        tree("foo")));
        validate(schema,
                tree("root",
                        tree("foo"),
                        tree("foo")));
        thrown.expect(ValidationException.class);
        validate(schema,
                tree("root",
                        tree("foo"),
                        tree("foo"),
                        tree("foo")));
    }


    private static void validate(Tree schema, Tree subject) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        XmlDocumentGenerator.toXml(schema, new StreamResult(buffer));
//        XmlDocumentGenerator.toXml(schema, new StreamResult(new File("sample.xml")));

        Validator validator = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                .newSchema(new StreamSource(new ByteArrayInputStream(buffer.toByteArray())))
                .newValidator();

        try {
            validator.validate(new DOMSource(XmlDocumentGenerator.toDocument(subject)));
        } catch (SAXException e) {
            throw new ValidationException(e);
        }
    }

    public static class ValidationException extends RuntimeException {

        public ValidationException() {
        }

        public ValidationException(Throwable cause) {
            super(cause);
        }

        public ValidationException(String message) {
            super(message);
        }

        public ValidationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
