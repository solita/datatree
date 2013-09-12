// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree.xml;

import fi.solita.datatree.Tree;
import org.junit.Test;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

import static fi.solita.datatree.Tree.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class XmlDocumentGeneratorTest {

    private static final String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    @Test
    public void empty_elements() {
        assertThat(toXml(
                tree("foo")),
                is(HEADER + "<foo/>"));
    }

    @Test
    public void text_content() {
        assertThat(toXml(
                tree("foo", "bar")),
                is(HEADER + "<foo>bar</foo>"));
    }

    @Test
    public void nested_elements() {
        assertThat(toXml(
                tree("root",
                        tree("foo", "1"),
                        tree("bar", "2"))),
                is(HEADER + "<root><foo>1</foo><bar>2</bar></root>"));
    }

    @Test
    public void attributes() {
        assertThat(toXml(
                tree("root",
                        meta("a", "1"),
                        meta("b", "2"))),
                is(HEADER + "<root a=\"1\" b=\"2\"/>"));
    }

    @Test
    public void there_are_no_empty_text_nodes() throws Exception {
        Tree t = tree("root", "");

        Element root = XmlDocumentGenerator.toDocument(t).getDocumentElement();

        assertThat(root.getNodeName(), is("root"));
        assertThat("child nodes", root.getChildNodes().getLength(), is(0));
        testAgainstReferenceImpl(t);
    }

    @Test
    public void default_namespace_set_in_current_node() throws Exception {
        Tree t = tree("root",
                meta("xmlns", "http://foo"));

        Element root = XmlDocumentGenerator.toDocument(t)
                .getDocumentElement();

        assertQNameLNameNS(root, "root", "root", "http://foo");
        testAgainstReferenceImpl(t);
    }

    @Test
    public void prefixed_namespace_set_in_current_node() throws Exception {
        Tree t = tree("f:root",
                meta("xmlns:f", "http://foo"));

        Element root = XmlDocumentGenerator.toDocument(t)
                .getDocumentElement();

        assertQNameLNameNS(root, "f:root", "root", "http://foo");
        testAgainstReferenceImpl(t);
    }

    @Test
    public void default_namespace_set_in_parent_node() throws Exception {
        Tree t = tree("parent",
                meta("xmlns", "http://foo"),
                tree("child"));

        Element child = (Element) XmlDocumentGenerator.toDocument(t)
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

        Element child = (Element) XmlDocumentGenerator.toDocument(t)
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

        Element child = (Element) XmlDocumentGenerator.toDocument(t)
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

        Element child = (Element) XmlDocumentGenerator.toDocument(t)
                .getDocumentElement()
                .getChildNodes().item(0)
                .getChildNodes().item(0);

        assertQNameLNameNS(child, "f:child", "child", "http://overloaded");
        testAgainstReferenceImpl(t);
    }

    @Test
    public void sets_the_namespace_for_default_xmlns_attribute() throws Exception {
        Tree t = tree("root", meta("xmlns", "http://foo"));

        Attr attr = XmlDocumentGenerator.toDocument(t)
                .getDocumentElement()
                .getAttributeNode("xmlns");

        assertQNameLNameNS(attr, "xmlns", "xmlns", "http://www.w3.org/2000/xmlns/");
        testAgainstReferenceImpl(t);
    }

    @Test
    public void sets_the_namespace_for_prefixed_xmlns_attribute() throws Exception {
        Tree t = tree("root", meta("xmlns:foo", "http://foo"));

        Attr attr = XmlDocumentGenerator.toDocument(t)
                .getDocumentElement()
                .getAttributeNode("xmlns:foo");

        assertQNameLNameNS(attr, "xmlns:foo", "foo", "http://www.w3.org/2000/xmlns/");
        testAgainstReferenceImpl(t);
    }

    // TODO: namespaces of other attributes


    private static void assertQNameLNameNS(Node node, String nodeName, String localName, String namespace) {
        assertThat("node name", node.getNodeName(), is(nodeName));
        assertThat("local name", node.getLocalName(), is(localName));
        assertThat("namespace URI", node.getNamespaceURI(), is(namespace));
    }

    private static void testAgainstReferenceImpl(Tree tree) throws Exception {
        Document actual = XmlDocumentGenerator.toDocument(tree);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        XmlDocumentGenerator.toXml(tree, new StreamResult(buffer));
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document expected = factory.newDocumentBuilder().parse(new ByteArrayInputStream(buffer.toByteArray()));

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

    private static String toXml(Tree tree) {
        try {
            StringWriter result = new StringWriter();
            XmlDocumentGenerator.toXml(tree, new StreamResult(result));
            return result.toString();

        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }
}
