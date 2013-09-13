// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree.xml;

import fi.solita.datatree.Tree;
import org.junit.Test;
import org.w3c.dom.*;

import static fi.solita.datatree.Tree.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class XmlNamespaceTest {

    @Test
    public void no_namespace() throws Exception {
        Tree t = tree("element",
                meta("attr", ""));

        Element element = XmlDocumentGenerator.toNamespaceAwareDocument(t)
                .getDocumentElement();
        Attr attr = (Attr) element.getAttributes().item(0);

        assertQNameLNameNS(element, "element", "element", null);
        assertQNameLNameNS(attr, "attr", "attr", null);
    }

    @Test
    public void default_namespace_set_in_current_node() throws Exception {
        Tree t = tree("element",
                meta("attr", ""),
                meta("xmlns", "http://foo"));

        Element element = XmlDocumentGenerator.toNamespaceAwareDocument(t)
                .getDocumentElement();
        Attr attr = (Attr) element.getAttributes().item(0);

        assertQNameLNameNS(element, "element", "element", "http://foo");
        assertQNameLNameNS(attr, "attr", "attr", null);
    }

    @Test
    public void prefixed_namespace_set_in_current_node() throws Exception {
        Tree t = tree("f:element",
                meta("f:attr", ""),
                meta("xmlns:f", "http://foo"));

        Element element = XmlDocumentGenerator.toNamespaceAwareDocument(t)
                .getDocumentElement();
        Attr attr = (Attr) element.getAttributes().item(0);

        assertQNameLNameNS(element, "f:element", "element", "http://foo");
        assertQNameLNameNS(attr, "f:attr", "attr", "http://foo");
    }

    @Test
    public void default_namespace_set_in_parent_node() throws Exception {
        Tree t = tree("parent",
                meta("xmlns", "http://foo"),
                tree("element",
                        meta("attr", "")));

        Element element = (Element) XmlDocumentGenerator.toNamespaceAwareDocument(t)
                .getDocumentElement()
                .getChildNodes().item(0);
        Attr attr = (Attr) element.getAttributes().item(0);

        assertQNameLNameNS(element, "element", "element", "http://foo");
        assertQNameLNameNS(attr, "attr", "attr", null);
    }

    @Test
    public void prefixed_namespace_set_in_parent_node() throws Exception {
        Tree t = tree("f:parent",
                meta("xmlns:f", "http://foo"),
                tree("f:element",
                        meta("f:attr", "")));

        Element element = (Element) XmlDocumentGenerator.toNamespaceAwareDocument(t)
                .getDocumentElement()
                .getChildNodes().item(0);
        Attr attr = (Attr) element.getAttributes().item(0);

        assertQNameLNameNS(element, "f:element", "element", "http://foo");
        assertQNameLNameNS(attr, "f:attr", "attr", "http://foo");
    }

    @Test
    public void default_namespace_overloaded_in_parent_node() throws Exception {
        Tree t = tree("root",
                meta("xmlns", "http://original"),
                tree("parent",
                        meta("xmlns", "http://overloaded"),
                        tree("element",
                                meta("attr", ""))));

        Element element = (Element) XmlDocumentGenerator.toNamespaceAwareDocument(t)
                .getDocumentElement()
                .getChildNodes().item(0)
                .getChildNodes().item(0);
        Attr attr = (Attr) element.getAttributes().item(0);

        assertQNameLNameNS(element, "element", "element", "http://overloaded");
        assertQNameLNameNS(attr, "attr", "attr", null);
    }

    @Test
    public void prefixed_namespace_overloaded_in_parent_node() throws Exception {
        Tree t = tree("f:root",
                meta("xmlns:f", "http://original"),
                tree("f:parent",
                        meta("xmlns:f", "http://overloaded"),
                        tree("f:element",
                                meta("f:attr", ""))));

        Element element = (Element) XmlDocumentGenerator.toNamespaceAwareDocument(t)
                .getDocumentElement()
                .getChildNodes().item(0)
                .getChildNodes().item(0);
        Attr attr = (Attr) element.getAttributes().item(0);

        assertQNameLNameNS(element, "f:element", "element", "http://overloaded");
        assertQNameLNameNS(attr, "f:attr", "attr", "http://overloaded");
    }

    @Test
    public void namespace_of_default_xmlns_attribute() throws Exception {
        Tree t = tree("root", meta("xmlns", "http://foo"));

        Attr attr = (Attr) XmlDocumentGenerator.toNamespaceAwareDocument(t)
                .getDocumentElement()
                .getAttributes().item(0);

        assertQNameLNameNS(attr, "xmlns", "xmlns", "http://www.w3.org/2000/xmlns/");
    }

    @Test
    public void namespace_of_prefixed_xmlns_attribute() throws Exception {
        Tree t = tree("root", meta("xmlns:foo", "http://foo"));

        Attr attr = (Attr) XmlDocumentGenerator.toNamespaceAwareDocument(t)
                .getDocumentElement()
                .getAttributes().item(0);

        assertQNameLNameNS(attr, "xmlns:foo", "foo", "http://www.w3.org/2000/xmlns/");
    }


    private static void assertQNameLNameNS(Node node, String nodeName, String localName, String namespace) {
        assertThat("node name", node.getNodeName(), is(nodeName));
        assertThat("local name", node.getLocalName(), is(localName));
        assertThat("namespace URI", node.getNamespaceURI(), is(namespace));
    }
}
