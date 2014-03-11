// Copyright © 2013-2014 Solita Oy <www.solita.fi>
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

    private final Source schema;
    private boolean used = false;

    public XmlSchemaValidator(Tree schema) {
        this(toSource(schema));
    }

    public XmlSchemaValidator(URL schema) {
        this(toSource(schema));
    }

    public XmlSchemaValidator(Source schema) {
        this.schema = schema;
    }

    public void validate(Tree subject) throws ValidationException {
        validate(toSource(subject));
    }

    public void validate(Source subject) throws ValidationException {
        ensureUsedOnlyOnce();
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

    private synchronized void ensureUsedOnlyOnce() {
        if (used) {
            throw new IllegalStateException("this validator has already been used once; create a new instance and use it instead");
        }
        used = true;
    }

    private static Source toSource(Tree tree) {
        return new StreamSource(XmlGenerator.toInputStream(tree));
    }

    private static Source toSource(URL url) {
        return new StreamSource(url.toString());
    }
}
