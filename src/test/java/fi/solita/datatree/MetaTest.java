// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree;

import org.junit.*;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class MetaTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void has_a_name() {
        Meta m = new Meta("name", "value");

        assertThat(m.name(), is("name"));
    }

    @Test
    public void name_cannot_be_null() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("name");
        new Meta(null, "value");
    }

    @Test
    public void has_a_value() {
        Meta m = new Meta("name", "value");

        assertThat(m.value(), is("value"));
    }

    @Test
    public void value_cannot_be_null() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("value");
        new Meta("name", null);
    }

    @Test
    public void to_string() {
        assertThat(new Meta("name", "value").toString(), is("{name \"value\"}"));
        assertThat(new Meta("name", "").toString(), is("{name}"));
    }

    @Test
    public void is_value_object() {
        Meta m = new Meta("a", "b");
        assertTrue("equals: itself", m.equals(m));
        assertTrue("equals: same name and value", new Meta("a", "b").equals(new Meta("a", "b")));

        assertFalse("equals: different name", new Meta("a", "b").equals(new Meta("x", "b")));
        assertFalse("equals: different value", new Meta("a", "b").equals(new Meta("a", "x")));

        assertFalse("equals: null", new Meta("a", "b").equals(null));
        assertFalse("equals: another type", new Meta("a", "b").equals(new Object()));

        assertEquals("hashCode",
                new Meta("a", "b").hashCode(),
                new Meta("a", "b").hashCode());
    }
}
