// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree;

import fi.solita.datatree.xml.*;
import org.junit.Test;

import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

import static fi.solita.datatree.Tree.*;
import static fi.solita.datatree.xml.XmlSchema.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class ExamplesTest {

    @Test
    public void element_with_text() {
        Tree t = tree("element", "some text");

        assertThat(toXml(t), containsString("<element>some text</element>"));
    }

    @Test
    public void element_with_attributes() {
        Tree t = tree("element", meta("attribute", "value"));

        assertThat(toXml(t), containsString("<element attribute=\"value\"/>"));
    }

    @Test
    public void nested_elements() {
        Tree t = tree("outer", tree("inner-1"), tree("inner-2"));

        assertThat(toXml(t), containsString("<outer><inner-1/><inner-2/></outer>"));
    }

    @Test
    public void convert_to_XML() throws Exception {
        OutputStream outputStream = new ByteArrayOutputStream();
        XmlDocumentGenerator.toXml(tree("some-tree"), new StreamResult(outputStream));
    }

    @Test
    public void xml_schemas() {
        Tree schema = schema(element("foo"));
        Tree document = tree("foo");
        XmlSchemaValidator.validate(schema, document);
    }

    @Test
    public void other_APIs_mentioned_in_README() throws Exception {
        XmlDocumentGenerator.toDocument(tree("foo"));
    }


    // Meta tests

    @Test
    public void code_samples_in_README_are_in_sync_with_this_example() throws IOException {
        String readme = read(Paths.get("README.md"));
        readme = readme.substring(0, readme.indexOf("Version History")); // ignore old method names etc.

        String examples = read(Paths.get("src/test/java").resolve(getClass().getName().replace('.', '/') + ".java"));
        examples = examples.replace("\\\"", "\"");          // unescape XML in strings
        examples = examples.replaceAll("(?m)^\\s+", "");    // remove indentation

        for (String codeSample : findCodeSamples(readme)) {
            if (codeSample.startsWith("<dependency>")) {    // ignore Maven dependency instructions
                continue;
            }
            assertThat(examples, containsString(codeSample));
        }
    }

    private static List<String> findCodeSamples(String documentation) {
        Matcher m = Pattern.compile("`(``)?(.*?)`(``)?", Pattern.MULTILINE | Pattern.DOTALL).matcher(documentation);
        List<String> results = new ArrayList<>();
        while (m.find()) {
            results.add(m.group(2).trim());
        }
        return results;
    }

    private static String read(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    private static String toXml(Tree tree) {
        try {
            StringWriter writer = new StringWriter();
            XmlDocumentGenerator.toXml(tree, new StreamResult(writer));
            return writer.toString();
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }
}
