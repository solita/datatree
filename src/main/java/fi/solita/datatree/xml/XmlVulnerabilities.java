// Copyright © 2013-2014 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree.xml;

import org.xml.sax.helpers.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;

public class XmlVulnerabilities {

    public static void check(InputStream input) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.newSAXParser().parse(input, new DefaultHandler());
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
