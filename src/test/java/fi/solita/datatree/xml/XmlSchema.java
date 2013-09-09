// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree.xml;

import fi.solita.datatree.*;

import java.util.*;

import static fi.solita.datatree.Tree.*;

public class XmlSchema {

    public static Tree schema(Object... body) {
        return tree("xs:schema", meta("xmlns:xs", "http://www.w3.org/2001/XMLSchema"), body);
    }


    // Elements

    public static Tree element(String name, Object... body) {
        return tree("xs:element", meta("name", name), body);
    }

    public static Tree all(Object... body) {
        return tree("xs:all", body);
    }

    public static Tree sequence(Object... body) {
        return tree("xs:sequence", body);
    }

    public static final int UNBOUNDED = Integer.MAX_VALUE;

    public static Meta maxOccurs(int n) {
        return meta("maxOccurs", n == UNBOUNDED ? "unbounded" : String.valueOf(n));
    }


    // Attributes

    public static Tree attribute(String name, Object... body) {
        return tree("xs:attribute", meta("name", name), body);
    }

    public static Meta required() {
        return meta("use", "required");
    }

    public static Meta type(String type) {
        return meta("type", type);
    }


    // Types

    public static Tree simpleType(Object... body) {
        return tree("xs:simpleType", body);
    }

    public static Tree complexType(Object... body) {
        return tree("xs:complexType", body);
    }

    public static Tree simpleContent(Object... body) {
        return tree("xs:simpleContent", body);
    }

    public static Tree restriction(String base, Object... body) {
        return tree("xs:restriction", meta("base", base), body);
    }

    public static Tree extension(String base, Object... body) {
        return tree("xs:extension", meta("base", base), body);
    }

    public static Meta ignoreWhitespace() {
        return mixed(false);
    }

    public static Meta mixed(boolean b) {
        return meta("mixed", String.valueOf(b));
    }


    // Restrictions

    public static List<Tree> enumeration(String... values) {
        List<Tree> enumeration = new ArrayList<>(values.length);
        for (String value : values) {
            enumeration.add(tree("xs:enumeration", meta("value", value)));
        }
        return enumeration;
    }
}
