// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree;

import fi.solita.datatree.xml.*;
import org.junit.Test;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

import static fi.solita.datatree.Tree.*;
import static fi.solita.datatree.xml.XmlSchema.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ExamplesTest {

    @Test
    public void element_with_text() {
        Tree t = tree("element", "some text");

        assertThat(XmlGenerator.toString(t), containsString("<element>some text</element>"));
    }

    @Test
    public void element_with_attributes() {
        Tree t = tree("element", meta("attribute", "value"));

        assertThat(XmlGenerator.toString(t), containsString("<element attribute=\"value\"/>"));
    }

    @Test
    public void nested_elements() {
        Tree t = tree("outer", tree("inner-1"), tree("inner-2"));

        assertThat(XmlGenerator.toString(t), containsString("<outer><inner-1/><inner-2/></outer>"));
    }

    @Test
    public void convert_to_XML() throws Exception {
        Tree tree = tree("some-tree");
        InputStream in = XmlGenerator.toInputStream(tree);

        assertThat(IOUtil.toString(in, StandardCharsets.UTF_8), containsString("<some-tree/>"));
    }

    @Test
    public void xml_schemas() {
        Tree schema = schema(element("foo"));
        Tree document = tree("foo");
        XmlSchemaValidator.validate(schema, document);
    }

    @Test
    public void other_APIs_mentioned_in_README() throws Exception {
        XmlGenerator.toDocument(tree("foo"));
    }


    // tests for the documentation

    public static final Path README = Paths.get("README.md");

    @Test
    public void code_samples_in_README_are_in_sync_with_this_example() throws IOException {
        String readme = IOUtil.toString(README, StandardCharsets.UTF_8);
        readme = readme.substring(0, readme.indexOf("Version History")); // ignore old method names etc.

        String examples = IOUtil.toString(Paths.get("src/test/java").resolve(getClass().getName().replace('.', '/') + ".java"), StandardCharsets.UTF_8);
        examples = examples.replace("\\\"", "\"");          // unescape XML in strings
        examples = examples.replaceAll("(?m)^\\s+", "");    // remove indentation

        for (String codeSample : findCodeSamples(readme)) {
            if (codeSample.startsWith("<dependency>")) {    // ignore Maven dependency instructions
                continue;
            }
            assertThat(examples, containsString(codeSample));
        }
    }

    @Test
    public void links_in_README_are_working() throws Exception {
        TestEnvironment.assumeSlowTestsEnabled();

        String readme = IOUtil.toString(README, StandardCharsets.UTF_8);
        for (URL url : findLinks(readme)) {
            System.out.println("Checking URL: " + url);
            String status = url.openConnection().getHeaderField("Status");

            assertThat("status of " + url, status, is("200 OK"));
        }
    }


    // helpers

    private static List<String> findCodeSamples(String documentation) {
        Matcher m = Pattern.compile("`(``)?(.*?)`(``)?", Pattern.MULTILINE | Pattern.DOTALL).matcher(documentation);
        List<String> results = new ArrayList<>();
        while (m.find()) {
            results.add(m.group(2).trim());
        }
        return results;
    }

    private static List<URL> findLinks(String documentation) throws MalformedURLException {
        Matcher m = Pattern.compile("https?://[-A-Za-z0-9+&@#/%?=~_|!:,.;]*[-A-Za-z0-9+&@#/%=~_|]").matcher(documentation);
        List<URL> results = new ArrayList<>();
        while (m.find()) {
            results.add(new URL(m.group()));
        }
        return results;
    }
}
