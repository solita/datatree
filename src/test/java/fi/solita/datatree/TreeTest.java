// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree;


import org.junit.*;
import org.junit.rules.ExpectedException;

import static fi.solita.datatree.Tree.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class TreeTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void has_a_name() {
        Tree t = tree("name");

        assertThat(t.name(), is("name"));
    }

    @Test
    public void name_cannot_be_null() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("name");
        tree(null);
    }

    @Test
    public void may_contain_text() {
        Tree t = tree("name", "content");

        assertThat(t.content(), is("content"));
    }

    @Test
    public void content_cannot_be_null() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("content");
        tree("name", (String) null);
    }

    @Test
    public void may_contain_child_trees() {
        Tree t = tree("root", tree("child 1"), tree("child 2"));

        assertThat(t.children(), contains(tree("child 1"), tree("child 2")));
    }

    @Test
    public void may_contain_meta() {
        Tree t = tree("root", meta("a", "foo"), meta("b", "bar"));

        assertThat(t.metae(), contains(meta("a", "foo"), meta("b", "bar")));
        assertThat("query by key", t.meta("a"), is("foo"));
        assertThat("query by non-existing key", t.meta("no such key"), is(""));
    }

    @Test
    public void may_contain_both_meta_and_child_trees() {
        Tree t = tree("root", meta("a", "1"), tree("b", "2"));

        assertThat(t.metae(), contains(meta("a", "1")));
        assertThat(t.children(), contains(tree("b", "2")));
    }

    @Test
    public void to_string() {
        assertThat("empty tree", tree("a").toString(), is("(a)"));
        assertThat("text content", tree("a", "txt").toString(), is("(a \"txt\")"));
        assertThat("meta", tree("a", meta("m1", "n1"), meta("m2", "n2")).toString(), is("(a {m1 \"n1\"} {m2 \"n2\"})"));
        assertThat("children", tree("a", tree("b"), tree("c")).toString(), is("(a (b) (c))"));

        assertThat("meta is always before children",
                tree("a",
                        tree("b"),
                        meta("m", ""),
                        tree("c")
                ).toString(), is("(a {m} (b) (c))"));
    }

    @Test
    public void is_value_object() {
        assertTrue("equals: same name", tree("a").equals(tree("a")));
        assertTrue("equals: same content", tree("a", "txt").equals(tree("a", "txt")));
        assertTrue("equals: same meta", tree("a", meta("m", "n")).equals(tree("a", meta("m", "n"))));
        assertTrue("equals: same children", tree("a", tree("b")).equals(tree("a", tree("b"))));

        assertFalse("equals: different name", tree("a").equals(tree("x")));
        assertFalse("equals: different content", tree("a", "content").equals(tree("a", "x")));
        assertFalse("equals: different meta name", tree("a", meta("m", "n")).equals(tree("a", meta("x", "n"))));
        assertFalse("equals: different meta value", tree("a", meta("m", "n")).equals(tree("a", meta("m", "x"))));
        assertFalse("equals: different children", tree("a", tree("b")).equals(tree("a", tree("x"))));

        assertFalse("equals: null", tree("a").equals(null));
        assertFalse("equals: another type", tree("a").equals(new Object()));

        assertEquals("hashCode",
                tree("root", meta("a", "1"), tree("b", "2")).hashCode(),
                tree("root", meta("a", "1"), tree("b", "2")).hashCode());
    }
}
