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

import static fi.solita.datatree.Tree.tree;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class GeneratingXmlDocumentsTest {

    @Test
    public void simple_document() {
        assertThat(toXml(tree("foo", "bar")),
                is("<?xml version=\"1.0\" encoding=\"UTF-8\"?><foo>bar</foo>"));
    }

    private static String toXml(Tree tree) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            document.setXmlStandalone(true);

            Element root = document.createElement(tree.name());
            root.appendChild(document.createTextNode(tree.content()));
            document.appendChild(root);

            StringWriter result = new StringWriter();
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(result));
            return result.toString();

        } catch (ParserConfigurationException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }
}
