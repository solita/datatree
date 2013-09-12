// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree.xml;

import fi.solita.datatree.Tree;
import org.junit.Test;
import org.w3c.dom.*;

import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

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
    public void default_namespace_set_in_current_node() {
        Tree t = tree("root",
                meta("xmlns", "http://foo"));

        Element root = XmlDocumentGenerator.toDocument(t)
                .getDocumentElement();

        assertLocalNamePrefixNamespace(root, "root", null, "http://foo");
    }

    @Test
    public void prefixed_namespace_set_in_current_node() {
        Tree t = tree("f:root",
                meta("xmlns:f", "http://foo"));

        Element root = XmlDocumentGenerator.toDocument(t)
                .getDocumentElement();

        assertLocalNamePrefixNamespace(root, "root", "f", "http://foo");
    }

    @Test
    public void default_namespace_set_in_parent_node() {
        Tree t = tree("parent",
                meta("xmlns", "http://foo"),
                tree("child"));

        Element child = (Element) XmlDocumentGenerator.toDocument(t)
                .getDocumentElement()
                .getChildNodes().item(1);

        assertLocalNamePrefixNamespace(child, "child", null, "http://foo");
    }

    @Test
    public void prefixed_namespace_set_in_parent_node() {
        Tree t = tree("f:parent",
                meta("xmlns:f", "http://foo"),
                tree("f:child"));

        Element child = (Element) XmlDocumentGenerator.toDocument(t)
                .getDocumentElement()
                .getChildNodes().item(1);

        assertLocalNamePrefixNamespace(child, "child", "f", "http://foo");
    }

    @Test
    public void default_namespace_overloaded_in_parent_node() {
        Tree t = tree("root",
                meta("xmlns", "http://original"),
                tree("parent",
                        meta("xmlns", "http://overloaded"),
                        tree("child")));

        Element child = (Element) XmlDocumentGenerator.toDocument(t)
                .getDocumentElement()
                .getChildNodes().item(1)
                .getChildNodes().item(1);

        assertLocalNamePrefixNamespace(child, "child", null, "http://overloaded");
    }

    @Test
    public void prefixed_namespace_overloaded_in_parent_node() {
        Tree t = tree("f:root",
                meta("xmlns:f", "http://original"),
                tree("f:parent",
                        meta("xmlns:f", "http://overloaded"),
                        tree("f:child")));

        Element child = (Element) XmlDocumentGenerator.toDocument(t)
                .getDocumentElement()
                .getChildNodes().item(1)
                .getChildNodes().item(1);

        assertLocalNamePrefixNamespace(child, "child", "f", "http://overloaded");
    }

    @Test
    public void sets_the_namespace_for_default_xmlns_attribute() {
        Tree t = tree("root", meta("xmlns", "http://foo"));

        Attr attr = XmlDocumentGenerator.toDocument(t)
                .getDocumentElement()
                .getAttributeNode("xmlns");

        assertQNameLNameNS(attr, "xmlns", "xmlns", "http://www.w3.org/2000/xmlns/");
    }

    @Test
    public void sets_the_namespace_for_prefixed_xmlns_attribute() {
        Tree t = tree("root", meta("xmlns:foo", "http://foo"));

        Attr attr = XmlDocumentGenerator.toDocument(t)
                .getDocumentElement()
                .getAttributeNode("xmlns:foo");

        assertQNameLNameNS(attr, "xmlns:foo", "foo", "http://www.w3.org/2000/xmlns/");
    }

    // TODO: namespaces of other attributes


    private static void assertQNameLNameNS(Attr attr, String name, String localName, String namespace) {
        assertThat(attr.getName(), is(name));
        assertThat(attr.getLocalName(), is(localName));
        assertThat(attr.getNamespaceURI(), is(namespace));
    }

    private static void assertLocalNamePrefixNamespace(Element child, String localName, String prefix, String namespace) {
        assertThat("local name", child.getLocalName(), is(localName));
        assertThat("prefix", child.getPrefix(), is(prefix));
        assertThat("namespace URI", child.getNamespaceURI(), is(namespace));
        if (prefix == null) {
            assertThat("tag name", child.getTagName(), is(localName));
        } else {
            assertThat("tag name", child.getTagName(), is(prefix + ":" + localName));
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
