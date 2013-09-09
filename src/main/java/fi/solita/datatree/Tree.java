// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree;

import java.util.*;

public final class Tree extends TreeOrMeta {

    private final String name;
    private final String text;
    private final List<Meta> metae;
    private final List<Tree> children;

    public static Tree tree(String name, Object... content) {
        return new Tree(name, Util.flatten(content));
    }

    public static Meta meta(String name, String value) {
        return new Meta(name, value);
    }

    private Tree(String name, Object[] content) {
        Objects.requireNonNull(name, "name must be non-null");
        this.name = name;
        this.text = Util.filterOneString(content);
        this.metae = Util.filterMeta(content);
        this.children = Util.filterTree(content);
    }

    public String name() {
        return name;
    }

    public String text() {
        return text;
    }

    public List<Meta> metae() {
        return metae;
    }

    public String meta(String name) {
        for (Meta meta : metae) {
            if (meta.name().equals(name)) {
                return meta.value();
            }
        }
        return "";
    }

    public List<Tree> children() {
        return children;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Tree)) {
            return false;
        }
        Tree that = (Tree) obj;
        return this.name.equals(that.name) &&
                this.text.equals(that.text) &&
                this.metae.equals(that.metae) &&
                this.children.equals(that.children);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + text.hashCode();
        result = 31 * result + metae.hashCode();
        result = 31 * result + children.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        sb.append(name);
        if (!text.isEmpty()) {
            sb.append(' ').append('"').append(text).append('"');
        }
        for (Meta meta : metae) {
            sb.append(' ').append(meta.toString());
        }
        for (Tree child : children) {
            sb.append(' ').append(child.toString());
        }
        sb.append(')');
        return sb.toString();
    }
}
