// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree;

import org.junit.Test;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

import static fi.solita.datatree.Tree.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class GeneratingXmlDocumentsTest {

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

    private static String toXml(Tree tree) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            document.setXmlStandalone(true);

            append(document, document, tree);

            StringWriter result = new StringWriter();
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(result));
            return result.toString();

        } catch (ParserConfigurationException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    private static void append(Document doc, Node parent, Tree tree) {
        Element current = doc.createElement(tree.name());
        current.appendChild(doc.createTextNode(tree.content()));
        parent.appendChild(current);

        for (Meta meta : tree.metae()) {
            current.setAttribute(meta.name(), meta.value());
        }
        for (Tree child : tree.children()) {
            append(doc, current, child);
        }
    }
}
