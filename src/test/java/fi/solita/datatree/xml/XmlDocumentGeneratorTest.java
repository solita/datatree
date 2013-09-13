// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree.xml;

import fi.solita.datatree.Tree;
import org.junit.Test;
import org.w3c.dom.*;

import java.io.InputStream;
import java.nio.charset.*;
import java.util.*;

import static fi.solita.datatree.Tree.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class XmlDocumentGeneratorTest {

    @Test
    public void empty_elements() {
        assertThat(XmlDocumentGenerator.toString(
                tree("foo")),
                is("<foo/>"));
    }

    @Test
    public void text_content() {
        assertThat(XmlDocumentGenerator.toString(
                tree("foo", "bar")),
                is("<foo>bar</foo>"));
    }

    @Test
    public void nested_elements() {
        assertThat(XmlDocumentGenerator.toString(
                tree("root",
                        tree("foo", "1"),
                        tree("bar", "2"))),
                is("<root><foo>1</foo><bar>2</bar></root>"));
    }

    @Test
    public void attributes() {
        assertThat(XmlDocumentGenerator.toString(
                tree("root",
                        meta("a", "1"),
                        meta("b", "2"))),
                is("<root a=\"1\" b=\"2\"/>"));
    }

    @Test
    public void can_produce_pretty_printed_strings() {
        assertThat(XmlDocumentGenerator.toPrettyString(
                tree("root",
                        tree("foo", "1"),
                        tree("bar", "2")))
                .replace("\r\n", "\n"),
                is("<root>\n" +
                        "  <foo>1</foo>\n" +
                        "  <bar>2</bar>\n" +
                        "</root>\n"));
    }

    @Test
    public void can_produce_input_streams() {
        InputStream in = XmlDocumentGenerator.toInputStream(tree("foo"));

        assertThat(toString(in, StandardCharsets.UTF_8),
                is("<?xml version=\"1.0\" encoding=\"UTF-8\"?><foo/>"));
    }

    @Test
    public void can_produce_namespace_unaware_documents() {
        Tree t = tree("root", meta("xmlns", "http://foo"));

        Element root = XmlDocumentGenerator.toDocument(t).getDocumentElement();

        assertThat(root.getNodeName(), is("root"));
        assertThat(root.getNamespaceURI(), is(nullValue()));
    }

    @Test
    public void can_produce_namespace_aware_documents() {
        Tree t = tree("root", meta("xmlns", "http://foo"));

        Element root = XmlDocumentGenerator.toNamespaceAwareDocument(t).getDocumentElement();

        assertThat(root.getNodeName(), is("root"));
        assertThat(root.getNamespaceURI(), is("http://foo"));
    }

    @Test
    public void the_produced_documents_will_not_have_empty_text_nodes() throws Exception {
        Tree t = tree("root", "");

        for (Document document : Arrays.asList(
                XmlDocumentGenerator.toDocument(t),
                XmlDocumentGenerator.toNamespaceAwareDocument(t))) {
            Element root = document.getDocumentElement();

            assertThat(root.getNodeName(), is("root"));
            assertThat("child nodes", root.getChildNodes().getLength(), is(0));
        }
    }


    private static String toString(InputStream in, Charset charset) {
        return new Scanner(in, charset.name()).useDelimiter("\\A").next();
    }
}
