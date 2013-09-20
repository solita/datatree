// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree.xml;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.ls.*;

import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MappedResourceResolver implements LSResourceResolver {

    private static DOMImplementationLS lsFactory;

    static {
        try {
            DOMImplementation impl = DocumentBuilderFactory.newInstance().newDocumentBuilder().getDOMImplementation();
            lsFactory = (DOMImplementationLS) impl;
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private final Map<String, String> systemIdsByNamespace = new ConcurrentHashMap<>();

    public MappedResourceResolver() {
        with(XMLConstants.XML_NS_URI, getClass().getResource("xml.xsd"));
    }

    public MappedResourceResolver with(String namespaceURI, URL newSystemId) {
        return with(namespaceURI, newSystemId.toString());
    }

    public MappedResourceResolver with(String namespaceURI, String newSystemId) {
        systemIdsByNamespace.put(namespaceURI, newSystemId);
        return this;
    }

    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        if (type.equals(XMLConstants.W3C_XML_SCHEMA_NS_URI)) {
            String newSystemId = systemIdsByNamespace.get(namespaceURI);
            if (newSystemId != null) {
                LSInput input = lsFactory.createLSInput();
                input.setSystemId(newSystemId);
                return input;
            }
        }
        return null;
    }
}
