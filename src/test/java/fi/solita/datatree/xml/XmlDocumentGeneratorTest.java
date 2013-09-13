// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree.xml;

import fi.solita.datatree.Tree;
import org.junit.Test;
import org.w3c.dom.Element;

import static fi.solita.datatree.Tree.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class XmlDocumentGeneratorTest {

    private static final String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    @Test
    public void empty_elements() {
        assertThat(XmlDocumentGenerator.toString(
                tree("foo")),
                is(HEADER + "<foo/>"));
    }

    @Test
    public void text_content() {
        assertThat(XmlDocumentGenerator.toString(
                tree("foo", "bar")),
                is(HEADER + "<foo>bar</foo>"));
    }

    @Test
    public void nested_elements() {
        assertThat(XmlDocumentGenerator.toString(
                tree("root",
                        tree("foo", "1"),
                        tree("bar", "2"))),
                is(HEADER + "<root><foo>1</foo><bar>2</bar></root>"));
    }

    @Test
    public void attributes() {
        assertThat(XmlDocumentGenerator.toString(
                tree("root",
                        meta("a", "1"),
                        meta("b", "2"))),
                is(HEADER + "<root a=\"1\" b=\"2\"/>"));
    }

    @Test
    public void can_produce_pretty_printed_xml() {
        assertThat(XmlDocumentGenerator.toPrettyString(
                tree("root",
                        tree("foo", "1"),
                        tree("bar", "2")))
                .replace("\r\n", "\n"),
                is(HEADER +
                        "<root>\n" +
                        "  <foo>1</foo>\n" +
                        "  <bar>2</bar>\n" +
                        "</root>\n"));
    }

    @Test
    public void the_produced_DOM_document_will_not_have_empty_text_nodes() throws Exception {
        Tree t = tree("root", "");

        Element root = XmlDocumentGenerator.toDocument(t).getDocumentElement();

        assertThat(root.getNodeName(), is("root"));
        assertThat("child nodes", root.getChildNodes().getLength(), is(0));
    }
}
