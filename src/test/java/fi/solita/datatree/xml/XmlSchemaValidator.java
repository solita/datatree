// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree.xml;

import fi.solita.datatree.Tree;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.*;
import javax.xml.validation.*;
import java.io.*;

public class XmlSchemaValidator {

    public static void validate(Tree schema, Tree subject) throws ValidationException {
        validate(schema, new DOMSource(XmlDocumentGenerator.toDocument(subject)));
    }

    public static void validate(Tree schema, Source subject) throws ValidationException {
        // XXX: We cannot just convert the schema to a Document, because namespaces would not work.
        // As a workaround we convert the schema to bytes and then read it back, same as a regular XSD file.
        try {
            ByteArrayOutputStream schemaBytes = new ByteArrayOutputStream();
            XmlDocumentGenerator.toXml(schema, new StreamResult(schemaBytes));
            validate(new StreamSource(new ByteArrayInputStream(schemaBytes.toByteArray())), subject);
        } catch (TransformerException e) {
            throw new ValidationException(e);
        }
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
}
