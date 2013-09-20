// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree.xml;

import fi.solita.datatree.Tree;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;
import java.io.IOException;
import java.net.URL;

public class XmlSchemaValidator {

    public static void validate(Tree schema, Tree subject) throws ValidationException {
        validate(toSource(schema), toSource(subject));
    }

    public static void validate(Tree schema, Source subject) throws ValidationException {
        validate(toSource(schema), subject);
    }

    public static void validate(URL schema, Tree subject) {
        validate(toSource(schema), toSource(subject));
    }

    public static void validate(URL schema, Source subject) {
        validate(toSource(schema), subject);
    }

    public static void validate(Source schema, Tree subject) throws ValidationException {
        validate(schema, toSource(subject));
    }

    public static void validate(Source schema, Source subject) throws ValidationException {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            factory.setResourceResolver(new MappedResourceResolver());
            Validator validator = factory
                    .newSchema(schema)
                    .newValidator();
            validator.validate(subject);
        } catch (SAXException | IOException e) {
            throw new ValidationException(e);
        }
    }

    private static Source toSource(Tree tree) {
        return new StreamSource(XmlGenerator.toInputStream(tree));
    }

    private static Source toSource(URL url) {
        return new StreamSource(url.toString());
    }
}
