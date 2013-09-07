// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree;

import java.util.*;

class Util {

    public static List<Meta> copyMeta(TreeOrMeta[] xs) {
        if (xs == null) {
            return Collections.emptyList();
        }
        List<Meta> result = new ArrayList<>(xs.length);
        for (TreeOrMeta x : xs) {
            if (x instanceof Meta) {
                result.add((Meta) x);
            }
        }
        return Arrays.asList(result.toArray(new Meta[result.size()]));
    }

    public static List<Tree> copyTrees(TreeOrMeta[] xs) {
        if (xs == null) {
            return Collections.emptyList();
        }
        List<Tree> result = new ArrayList<>(xs.length);
        for (TreeOrMeta x : xs) {
            if (x instanceof Tree) {
                result.add((Tree) x);
            }
        }
        return Arrays.asList(result.toArray(new Tree[result.size()]));
    }
}
