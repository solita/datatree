// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree.xml;

import fi.solita.datatree.Tree;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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

            // TODO: remove debug code
            System.out.println("-- from bytes");
            debug(readDoc(schemaBytes));
            System.out.println("-- from tree");
            debug(XmlDocumentGenerator.toDocument(schema));

            //StreamSource source = new StreamSource(new ByteArrayInputStream(schemaBytes.toByteArray()));
            DOMSource source = new DOMSource(XmlDocumentGenerator.toDocument(schema));

            validate(source, subject);
        } catch (TransformerException e) {
            throw new ValidationException(e);
        }
    }

    private static void debug(Document doc) {
        System.out.println("doc = " + doc);

        Element e = doc.getDocumentElement();
        System.out.println("e = " + e);
        System.out.println("e.getTagName() = " + e.getTagName());
        System.out.println("e.getLocalName() = " + e.getLocalName());
        System.out.println("e.getNamespaceURI() = " + e.getNamespaceURI());

        Attr attr = (Attr) e.getAttributes().item(0);
        System.out.println("e.getAttributes().item(0) = " + attr);
        System.out.println("attr.getName() = " + attr.getName());
        System.out.println("attr.getLocalName() = " + attr.getLocalName());
        System.out.println("attr.getNamespaceURI() = " + attr.getNamespaceURI());

        NodeList childNodes = e.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            System.out.println("child = " + child);
            System.out.println("child.getNodeName() = " + child.getNodeName());
            System.out.println("child.getLocalName() = " + child.getLocalName());
            System.out.println("child.getNamespaceURI() = " + child.getNamespaceURI());
        }
    }

    private static Document readDoc(ByteArrayOutputStream schemaBytes) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new ByteArrayInputStream(schemaBytes.toByteArray()));
        } catch (Exception e) {
            throw new RuntimeException(e);
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
