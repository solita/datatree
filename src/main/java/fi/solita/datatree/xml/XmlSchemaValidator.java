// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree.xml;

import fi.solita.datatree.Tree;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.*;
import java.io.IOException;

import static fi.solita.datatree.xml.XmlDocumentGenerator.toNamespaceAwareDocument;

public class XmlSchemaValidator {

    public static void validate(Tree schema, Tree subject) throws ValidationException {
        validate(toSource(schema), toSource(subject));
    }

    public static void validate(Source schema, Tree subject) throws ValidationException {
        validate(schema, toSource(subject));
    }

    public static void validate(Tree schema, Source subject) throws ValidationException {
        validate(toSource(schema), subject);
    }

    public static void validate(Source schema, Source subject) throws ValidationException {
        try {
            Validator validator = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                    .newSchema(schema)
                    .newValidator();
            validator.validate(subject);
        } catch (SAXException | IOException e) {
            throw new ValidationException(e);
        }
    }

    private static Source toSource(Tree schema) {
        return new DOMSource(toNamespaceAwareDocument(schema));
    }
}
