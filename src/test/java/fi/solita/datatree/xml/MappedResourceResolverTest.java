// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree.xml;

import org.junit.Test;
import org.w3c.dom.ls.LSInput;

import javax.xml.XMLConstants;
import java.net.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class MappedResourceResolverTest {

    @Test
    public void returns_the_replacement_systemId_for_known_namespaces() throws MalformedURLException {
        MappedResourceResolver resolver = new MappedResourceResolver()
                .with("http://example.com/foo", new URL("file://replacement.xsd"));

        LSInput input = resolver.resolveResource(XMLConstants.W3C_XML_SCHEMA_NS_URI,
                "http://example.com/foo", null, "http://example.com/foo.xsd", null);

        assertThat(input.getSystemId(), is("file://replacement.xsd"));
    }

    @Test
    public void returns_null_for_unknown_resources() {
        MappedResourceResolver resolver = new MappedResourceResolver();

        assertThat("unknown DTD",
                resolver.resolveResource(XMLConstants.XML_DTD_NS_URI,
                        null, "foo", "foo.dtd", null),
                is(nullValue()));

        assertThat("unknown XSD",
                resolver.resolveResource(XMLConstants.W3C_XML_SCHEMA_NS_URI,
                        "http://example.com/foo", null, "http://example.com/foo.xsd", null),
                is(nullValue()));
    }

    @Test
    public void has_the_XML_namespace_XSD_built_in() {
        MappedResourceResolver resolver = new MappedResourceResolver();

        LSInput input = resolver.resolveResource(XMLConstants.W3C_XML_SCHEMA_NS_URI, XMLConstants.XML_NS_URI, null, null, null);

        assertThat(input.getSystemId(), endsWith("fi/solita/datatree/xml/xml.xsd"));
    }
}
