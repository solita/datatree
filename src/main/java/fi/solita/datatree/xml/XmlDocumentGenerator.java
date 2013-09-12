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

public class XmlDocumentGenerator {

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

    /**
     * Makes {@code tree} a child node of {@code parent}.
     */
    public static void appendElement(Document document, Node parent, Tree tree) {
        String tagName = tree.name();

        String prefix;
        if (tagName.contains(":")) {
            String[] parts = tagName.split(":", 2);
            prefix = parts[0];
        } else {
            prefix = null;
        }

        String ns;
        if (prefix != null) {
            ns = tree.meta("xmlns:" + prefix);
        } else {
            ns = tree.meta("xmlns");
        }
        if (ns.isEmpty() && document.getDocumentElement() != null) {
            ns = parent.lookupNamespaceURI(prefix);
        }

        Element current = document.createElementNS(ns, tagName);
        parent.appendChild(current);

        String text = tree.text();
        if (!text.isEmpty()) {
            current.appendChild(document.createTextNode(text));
        }

        for (Meta meta : tree.metae()) {
            String name = meta.name();
            String attrNs = null;
            if (name.equals(XMLConstants.XMLNS_ATTRIBUTE) || name.startsWith(XMLConstants.XMLNS_ATTRIBUTE + ":")) {
                attrNs = XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
            }
            current.setAttributeNS(attrNs, name, meta.value());
        }
        for (Tree child : tree.children()) {
            appendElement(document, current, child);
        }
    }
}
