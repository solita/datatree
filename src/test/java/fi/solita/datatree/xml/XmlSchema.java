// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree.xml;

import fi.solita.datatree.*;

import static fi.solita.datatree.Tree.*;

public class XmlSchema {

    public static Tree schema(Object... body) {
        return tree("xs:schema", meta("xmlns:xs", "http://www.w3.org/2001/XMLSchema"), body);
    }

    public static Tree element(String name, Object... body) {
        return tree("xs:element", meta("name", name), body);
    }

    public static Tree complexType(Object... body) {
        return tree("xs:complexType", body);
    }

    public static Tree all(Object... body) {
        return tree("xs:all", body);
    }

    public static Tree sequence(Object... body) {
        return tree("xs:sequence", body);
    }

    public static Meta maxOccurs(int n) {
        return meta("maxOccurs", String.valueOf(n));
    }
}
