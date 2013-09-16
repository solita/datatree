// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree;

import org.junit.*;
import org.junit.rules.ExpectedException;

import static fi.solita.datatree.Tree.meta;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class MetaTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void has_a_name() {
        Meta m = meta("name", "value");

        assertThat(m.name(), is("name"));
    }

    @Test
    public void name_cannot_be_null() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("name");
        meta(null, "value");
    }

    @Test
    public void has_a_value() {
        Meta m = meta("name", "value");

        assertThat(m.value(), is("value"));
    }

    @Test
    public void value_cannot_be_null() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("value");
        meta("name", null);
    }

    @Test
    public void converts_unknown_types_to_string() {
        Meta m = meta("name", 42);

        assertThat(m, is(meta("name", "42")));
    }

    @Test
    public void to_string() {
        assertThat(meta("name", "value").toString(), is("{name \"value\"}"));
        assertThat(meta("name", "").toString(), is("{name}"));
    }

    @Test
    public void is_value_object() {
        Meta m = meta("a", "b");
        assertTrue("equals: itself", m.equals(m));
        assertTrue("equals: same name and value", meta("a", "b").equals(meta("a", "b")));

        assertFalse("equals: different name", meta("a", "b").equals(meta("x", "b")));
        assertFalse("equals: different value", meta("a", "b").equals(meta("a", "x")));

        assertFalse("equals: null", meta("a", "b").equals(null));
        assertFalse("equals: another type", meta("a", "b").equals(new Object()));

        assertEquals("hashCode",
                meta("a", "b").hashCode(),
                meta("a", "b").hashCode());
    }
}
