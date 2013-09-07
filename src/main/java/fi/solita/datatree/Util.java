// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree;

import java.util.*;

class Util {

    static List<Meta> copyMeta(Node[] nodes) {
        if (nodes == null) {
            return Collections.emptyList();
        }
        List<Meta> result = new ArrayList<>(nodes.length);
        for (Node node : nodes) {
            if (node instanceof Meta) {
                result.add((Meta) node);
            }
        }
        return Arrays.asList(result.toArray(new Meta[result.size()]));
    }

    static List<Tree> copyTrees(Node[] nodes) {
        if (nodes == null) {
            return Collections.emptyList();
        }
        List<Tree> result = new ArrayList<>(nodes.length);
        for (Node node : nodes) {
            if (node instanceof Tree) {
                result.add((Tree) node);
            }
        }
        return Arrays.asList(result.toArray(new Tree[result.size()]));
    }
}
