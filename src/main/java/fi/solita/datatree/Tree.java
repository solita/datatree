// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree;

import java.util.*;

public class Tree extends Node {

    private final String name;
    private final String content;
    private final List<Meta> meta;
    private final List<Tree> children;

    public static Tree tree(String name) {
        return new Tree(name, null, null);
    }

    public static Tree tree(String name, String content) {
        return new Tree(name, content, null);
    }

    public static Tree tree(String name, Node... children) {
        return new Tree(name, null, children);
    }

    public static Meta meta(String name, String value) {
        return new Meta(name, value);
    }

    private Tree(String name, String content, Node... nodes) {
        Objects.requireNonNull(name, "name must be non-null");
        this.name = name;
        this.content = content;
        this.meta = Util.copyMeta(nodes);
        this.children = Util.copyTrees(nodes);
    }

    public String name() {
        return name;
    }

    public String text() {
        return content;
    }

    public List<Meta> meta() {
        return meta;
    }

    public List<Tree> children() {
        return children;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tree)) {
            return false;
        }
        Tree that = (Tree) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.content, that.content) &&
                Objects.equals(this.meta, that.meta) &&
                Objects.equals(this.children, that.children);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        sb.append(name);
        if (content != null) {
            sb.append(' ').append('"').append(content).append('"');
        }
        for (Node child : meta) {
            sb.append(' ').append(child.toString());
        }
        for (Node child : children) {
            sb.append(' ').append(child.toString());
        }
        sb.append(')');
        return sb.toString();
    }
}
