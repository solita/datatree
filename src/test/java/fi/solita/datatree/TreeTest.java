// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree;


import org.junit.*;
import org.junit.rules.ExpectedException;

import java.util.*;

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
    public void cannot_contain_unsupported_types() {
        Object unsupported = new Object();

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot contain java.lang.Object instances; tried to add " + unsupported);
        tree("name", unsupported);
    }

    @Test
    public void may_have_text() {
        Tree t = tree("name", "text");

        assertThat(t.text(), is("text"));
    }

    @Test
    public void cannot_have_more_than_one_text() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot have more than one text string; tried to add \"bar\" when already had \"foo\"");
        tree("name", "foo", "bar");
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
    public void may_contain_both_meta_and_text() {
        Tree t = tree("root", meta("a", "1"), "some text");

        assertThat("metae", t.metae(), contains(meta("a", "1")));
        assertThat("text", t.text(), is("some text"));
    }

    @Test
    public void may_contain_both_meta_and_child_trees() {
        Tree t = tree("root", meta("a", "1"), tree("b", "2"));

        assertThat("metae", t.metae(), contains(meta("a", "1")));
        assertThat("children", t.children(), contains(tree("b", "2")));
    }

    @Test
    public void cannot_contain_both_text_and_child_trees() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot contain both text and trees; had text \"foo\" and children [(bar)]");
        tree("name", "foo", tree("bar"));
    }

    @Test
    public void will_flatten_array_arguments() {
        Tree[] ab = {tree("a"), tree("b")};
        Tree[] cd = {tree("c"), tree("d")};
        Tree t = tree("root", ab, cd);

        assertThat(t, is(
                tree("root",
                        tree("a"),
                        tree("b"),
                        tree("c"),
                        tree("d"))));
    }

    @Test
    public void will_flatten_collection_arguments() {
        List<Tree> ab = Arrays.asList(tree("a"), tree("b"));
        Collection<Tree> cd = Collections.unmodifiableCollection(Arrays.asList(tree("c"), tree("d")));
        Tree t = tree("root", ab, cd);

        assertThat(t, is(
                tree("root",
                        tree("a"),
                        tree("b"),
                        tree("c"),
                        tree("d"))));
    }

    @Test
    public void fill_flatten_null_arguments_as_if_they_were_empty_collections() {
        assertThat("null item", tree("root", (Object) null), is(tree("root")));
        assertThat("null list", tree("root", (Object[]) null), is(tree("root")));
    }

    @Test
    public void to_string() {
        assertThat("empty tree", tree("a").toString(), is("(a)"));
        assertThat("text", tree("a", "txt").toString(), is("(a \"txt\")"));
        assertThat("meta", tree("a", meta("m1", "n1"), meta("m2", "n2")).toString(), is("(a {m1 \"n1\"} {m2 \"n2\"})"));
        assertThat("children", tree("a", tree("b"), tree("c")).toString(), is("(a (b) (c))"));

        assertThat("meta is always before text",
                tree("a",
                        meta("m", ""),
                        "text",
                        meta("n", "")
                ).toString(), is("(a {m} {n} \"text\")"));
        assertThat("meta is always before children",
                tree("a",
                        tree("b"),
                        meta("m", ""),
                        tree("c")
                ).toString(), is("(a {m} (b) (c))"));
    }

    @Test
    public void is_value_object() {
        Tree t = tree("a");
        assertTrue("equals: itself", t.equals(t));
        assertTrue("equals: same name", tree("a").equals(tree("a")));
        assertTrue("equals: same text", tree("a", "txt").equals(tree("a", "txt")));
        assertTrue("equals: same meta", tree("a", meta("m", "n")).equals(tree("a", meta("m", "n"))));
        assertTrue("equals: same children", tree("a", tree("b")).equals(tree("a", tree("b"))));

        assertFalse("equals: different name", tree("a").equals(tree("x")));
        assertFalse("equals: different text", tree("a", "txt").equals(tree("a", "x")));
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
