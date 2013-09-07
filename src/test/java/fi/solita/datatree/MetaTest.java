// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class MetaTest {

    @Test
    public void to_string() {
        assertThat(new Meta("name", "value").toString(), is("{name \"value\"}"));
    }

    @Test
    public void is_value_object() {
        assertTrue("equals: same", new Meta("a", "b").equals(new Meta("a", "b")));
        assertFalse("equals: different name", new Meta("a", "b").equals(new Meta("x", "b")));
        assertFalse("equals: different value", new Meta("a", "b").equals(new Meta("a", "x")));
    }
}
