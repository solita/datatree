// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree.xml;

import fi.solita.datatree.Tree;
import org.junit.Test;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

import static fi.solita.datatree.Tree.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class XmlNamespaceTest {

    @Test
    public void default_namespace_set_in_current_node() throws Exception {
        Tree t = tree("root",
                meta("xmlns", "http://foo"));

        Element root = XmlDocumentGenerator.toNamespaceAwareDocument(t)
                .getDocumentElement();

        assertQNameLNameNS(root, "root", "root", "http://foo");
        testAgainstReferenceImpl(t);
    }

    @Test
    public void prefixed_namespace_set_in_current_node() throws Exception {
        Tree t = tree("f:root",
                meta("xmlns:f", "http://foo"));

        Element root = XmlDocumentGenerator.toNamespaceAwareDocument(t)
                .getDocumentElement();

        assertQNameLNameNS(root, "f:root", "root", "http://foo");
        testAgainstReferenceImpl(t);
    }

    @Test
    public void default_namespace_set_in_parent_node() throws Exception {
        Tree t = tree("parent",
                meta("xmlns", "http://foo"),
                tree("child"));

        Element child = (Element) XmlDocumentGenerator.toNamespaceAwareDocument(t)
                .getDocumentElement()
                .getChildNodes().item(0);

        assertQNameLNameNS(child, "child", "child", "http://foo");
        testAgainstReferenceImpl(t);
    }

    @Test
    public void prefixed_namespace_set_in_parent_node() throws Exception {
        Tree t = tree("f:parent",
                meta("xmlns:f", "http://foo"),
                tree("f:child"));

        Element child = (Element) XmlDocumentGenerator.toNamespaceAwareDocument(t)
                .getDocumentElement()
                .getChildNodes().item(0);

        assertQNameLNameNS(child, "f:child", "child", "http://foo");
        testAgainstReferenceImpl(t);
    }

    @Test
    public void default_namespace_overloaded_in_parent_node() throws Exception {
        Tree t = tree("root",
                meta("xmlns", "http://original"),
                tree("parent",
                        meta("xmlns", "http://overloaded"),
                        tree("child")));

        Element child = (Element) XmlDocumentGenerator.toNamespaceAwareDocument(t)
                .getDocumentElement()
                .getChildNodes().item(0)
                .getChildNodes().item(0);

        assertQNameLNameNS(child, "child", "child", "http://overloaded");
        testAgainstReferenceImpl(t);
    }

    @Test
    public void prefixed_namespace_overloaded_in_parent_node() throws Exception {
        Tree t = tree("f:root",
                meta("xmlns:f", "http://original"),
                tree("f:parent",
                        meta("xmlns:f", "http://overloaded"),
                        tree("f:child")));

        Element child = (Element) XmlDocumentGenerator.toNamespaceAwareDocument(t)
                .getDocumentElement()
                .getChildNodes().item(0)
                .getChildNodes().item(0);

        assertQNameLNameNS(child, "f:child", "child", "http://overloaded");
        testAgainstReferenceImpl(t);
    }

    @Test
    public void namespace_of_default_xmlns_attribute() throws Exception {
        Tree t = tree("root", meta("xmlns", "http://foo"));

        Attr attr = XmlDocumentGenerator.toNamespaceAwareDocument(t)
                .getDocumentElement()
                .getAttributeNode("xmlns");

        assertQNameLNameNS(attr, "xmlns", "xmlns", "http://www.w3.org/2000/xmlns/");
        testAgainstReferenceImpl(t);
    }

    @Test
    public void namespace_of_prefixed_xmlns_attribute() throws Exception {
        Tree t = tree("root", meta("xmlns:foo", "http://foo"));

        Attr attr = XmlDocumentGenerator.toNamespaceAwareDocument(t)
                .getDocumentElement()
                .getAttributeNode("xmlns:foo");

        assertQNameLNameNS(attr, "xmlns:foo", "foo", "http://www.w3.org/2000/xmlns/");
        testAgainstReferenceImpl(t);
    }

    @Test
    public void namespace_of_unprefixed_attribute_without_default_namespace() throws Exception {
        Tree t = tree("root",
                meta("attr", ""));

        Attr attr = XmlDocumentGenerator.toNamespaceAwareDocument(t)
                .getDocumentElement()
                .getAttributeNode("attr");

        assertQNameLNameNS(attr, "attr", "attr", null);
        testAgainstReferenceImpl(t);
    }

    @Test
    public void namespace_of_unprefixed_attribute_with_default_namespace() throws Exception {
        Tree t = tree("root",
                meta("attr", ""),
                meta("xmlns", "http://foo"));

        Attr attr = (Attr) XmlDocumentGenerator.toNamespaceAwareDocument(t)
                .getDocumentElement()
                .getAttributes().item(0);

        assertQNameLNameNS(attr, "attr", "attr", null);
        testAgainstReferenceImpl(t);
    }

    @Test
    public void namespace_of_prefixed_attribute_from_current_element() throws Exception {
        Tree t = tree("root",
                meta("foo:attr", ""),
                meta("xmlns:foo", "http://foo"));

        Attr attr = XmlDocumentGenerator.toNamespaceAwareDocument(t)
                .getDocumentElement()
                .getAttributeNode("foo:attr");

        assertQNameLNameNS(attr, "foo:attr", "attr", "http://foo");
        testAgainstReferenceImpl(t);
    }

    @Test
    public void namespace_of_prefixed_attribute_from_parent_element() throws Exception {
        Tree t = tree("root",
                meta("xmlns:foo", "http://foo"),
                tree("child",
                        meta("foo:attr", "")));

        Attr attr = ((Element) XmlDocumentGenerator.toNamespaceAwareDocument(t)
                .getDocumentElement()
                .getChildNodes().item(0))
                .getAttributeNode("foo:attr");

        assertQNameLNameNS(attr, "foo:attr", "attr", "http://foo");
        testAgainstReferenceImpl(t);
    }


    private static void assertQNameLNameNS(Node node, String nodeName, String localName, String namespace) {
        assertThat("node name", node.getNodeName(), is(nodeName));
        assertThat("local name", node.getLocalName(), is(localName));
        assertThat("namespace URI", node.getNamespaceURI(), is(namespace));
    }

    private static void testAgainstReferenceImpl(Tree tree) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        XmlDocumentGenerator.toXml(tree, new StreamResult(buffer));
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document expected = factory.newDocumentBuilder().parse(new ByteArrayInputStream(buffer.toByteArray()));

        Document actual = XmlDocumentGenerator.toNamespaceAwareDocument(tree);

        assertStructurallyEqual(actual, expected);
    }

    private static void assertStructurallyEqual(Node actual, Node expected) {
        assertThat("node name", actual.getNodeName(), is(expected.getNodeName()));
        assertThat("node type", actual.getNodeType(), is(expected.getNodeType()));
        assertThat("node value", actual.getNodeValue(), is(expected.getNodeValue()));
        assertThat("local name", actual.getLocalName(), is(expected.getLocalName()));
        assertThat("namespace URI", actual.getNamespaceURI(), is(expected.getNamespaceURI()));

        NamedNodeMap actualAttributes = actual.getAttributes();
        NamedNodeMap expectedAttributes = expected.getAttributes();
        assertThat("has attributes", actualAttributes != null, is(expectedAttributes != null));
        if (expectedAttributes != null && actualAttributes != null) {
            for (int i = 0; i < expectedAttributes.getLength(); i++) {
                assertStructurallyEqual(actualAttributes.item(i), expectedAttributes.item(i));
            }
        }

        NodeList actualChildren = actual.getChildNodes();
        NodeList expectedChildren = expected.getChildNodes();
        assertThat("child nodes", actualChildren.getLength(), is(expectedChildren.getLength()));
        for (int i = 0; i < expectedChildren.getLength(); i++) {
            assertStructurallyEqual(actualChildren.item(i), expectedChildren.item(i));
        }
    }
}
