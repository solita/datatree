// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree.xml;

import fi.solita.datatree.Tree;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.*;
import javax.xml.validation.*;
import java.io.*;

public class XmlSchemaValidator {

    public static void validate(Tree schema, Tree subject) throws Exception {
        validate(schema, new DOMSource(XmlDocumentGenerator.toDocument(subject)));
    }

    public static void validate(Tree schema, Source subject) throws Exception {
        // XXX: We cannot just convert the schema to a Document, because namespaces would not work.
        // As a workaround we convert the schema to bytes and then read it back, same as a regular XSD file.
        ByteArrayOutputStream schemaBytes = new ByteArrayOutputStream();
        XmlDocumentGenerator.toXml(schema, new StreamResult(schemaBytes));
//        XmlDocumentGenerator.toXml(schema, new StreamResult(new File("sample.xml")));
        validate(new StreamSource(new ByteArrayInputStream(schemaBytes.toByteArray())), subject);
    }

    public static void validate(Source schema, Source subject) throws Exception {
        Validator validator = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                .newSchema(schema)
                .newValidator();
        try {
            validator.validate(subject);
        } catch (SAXException e) {
            throw new ValidationException(e);
        }
    }
}
