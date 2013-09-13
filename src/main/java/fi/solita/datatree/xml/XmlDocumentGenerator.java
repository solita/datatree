// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree.xml;

import fi.solita.datatree.*;
import org.w3c.dom.*;

import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class XmlDocumentGenerator {

    private static final String XALAN_INDENT_AMOUNT = "{http://xml.apache.org/xalan}indent-amount";

    public static String toString(Tree tree) {
        try {
            StringWriter result = new StringWriter();
            toXml(tree, new StreamResult(result));
            return result.toString();

        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toPrettyString(Tree tree) {
        try {
            StringWriter result = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(XALAN_INDENT_AMOUNT, "2");
            transformer.transform(new DOMSource(XmlDocumentGenerator.toDocument(tree)), new StreamResult(result));
            return result.toString();

        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public static void toXml(Tree tree, Result result) throws TransformerException {
        Document document = toDocument(tree);
        TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), result);
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

    public static Document toNamespaceAwareDocument(Tree tree) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            toXml(tree, new StreamResult(buffer));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            return factory.newDocumentBuilder().parse(new ByteArrayInputStream(buffer.toByteArray()));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Makes {@code tree} a child node of {@code parent}.
     */
    public static void appendElement(Document document, Node parent, Tree tree) {
        Element current = document.createElementNS(
                findNamespace(document, parent, tree, tree.name()),
                tree.name()
        );
        parent.appendChild(current);

        String text = tree.text();
        if (!text.isEmpty()) {
            current.appendChild(document.createTextNode(text));
        }

        for (Meta meta : tree.metae()) {
            current.setAttributeNS(
                    findNamespace(document, parent, tree, meta.name()),
                    meta.name(),
                    meta.value()
            );
        }
        for (Tree child : tree.children()) {
            appendElement(document, current, child);
        }
    }

    private static String findNamespace(Document document, Node parent, Tree current, String qname) {
        if (qname.equals("xmlns") || qname.startsWith("xmlns:")) {
            return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
        }

        String prefix;
        if (qname.contains(":")) {
            String[] parts = qname.split(":", 2);
            prefix = parts[0];
        } else {
            prefix = null;
        }

        String ns;
        if (prefix != null) {
            ns = current.meta("xmlns:" + prefix);
        } else {
            ns = current.meta("xmlns");
        }
        if (ns.isEmpty() && document.getDocumentElement() != null) {
            ns = parent.lookupNamespaceURI(prefix);
        }
        return ns;
    }
}
