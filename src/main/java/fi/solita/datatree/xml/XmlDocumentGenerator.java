// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree.xml;

import fi.solita.datatree.*;
import org.w3c.dom.*;

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
        Element current = document.createElement(tree.name());
        current.appendChild(document.createTextNode(tree.text()));
        parent.appendChild(current);

        for (Meta meta : tree.metae()) {
            current.setAttribute(meta.name(), meta.value());
        }
        for (Tree child : tree.children()) {
            appendElement(document, current, child);
        }
    }
}
