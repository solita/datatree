// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree.xml;

import fi.solita.datatree.*;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class XmlDocumentGenerator {

    public static InputStream toInputStream(Tree tree) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(toDocument(tree)), new StreamResult(buffer));
            return new ByteArrayInputStream(buffer.toByteArray());

        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toString(Tree tree) {
        return toString(tree, 0);
    }

    public static String toPrettyString(Tree tree) {
        return toString(tree, 2);
    }

    private static String toString(Tree tree, int indent) {
        try {
            StringWriter result = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            // The encoding in the XML declaration will not be correct for strings,
            // so we omit it do discourage anyone from writing the string to file.
            // It is preferable to treat XML with the same care as binary data.
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            if (indent > 0) {
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", String.valueOf(indent));
            }
            transformer.transform(new DOMSource(toDocument(tree)), new StreamResult(result));
            return result.toString();

        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public static Document toNamespaceAwareDocument(Tree tree) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            return factory.newDocumentBuilder().parse(toInputStream(tree));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Document toDocument(Tree tree) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            document.setXmlStandalone(true);
            appendElement(document, document, tree);
            return document;

        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Makes {@code tree} a child node of {@code parent}.
     */
    private static void appendElement(Document document, Node parent, Tree tree) {
        Element current = document.createElement(tree.name());
        parent.appendChild(current);

        if (!tree.text().isEmpty()) {
            current.appendChild(document.createTextNode(tree.text()));
        }
        for (Meta meta : tree.metae()) {
            current.setAttribute(meta.name(), meta.value());
        }
        for (Tree child : tree.children()) {
            appendElement(document, current, child);
        }
    }
}
