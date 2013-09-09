// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree;

import java.util.*;

class Util {

    public static List<Meta> filterMeta(TreeOrMeta[] xs) {
        List<Meta> result = new ArrayList<>(xs.length);
        for (TreeOrMeta x : xs) {
            if (x instanceof Meta) {
                result.add((Meta) x);
            }
        }
        return Arrays.asList(result.toArray(new Meta[result.size()]));
    }

    public static List<Tree> filterTree(TreeOrMeta[] xs) {
        List<Tree> result = new ArrayList<>(xs.length);
        for (TreeOrMeta x : xs) {
            if (x instanceof Tree) {
                result.add((Tree) x);
            }
        }
        return Arrays.asList(result.toArray(new Tree[result.size()]));
    }

    public static TreeOrMeta[] flatten(Object[] maybeArrays) {
        List<TreeOrMeta> results = new ArrayList<>();
        for (Object maybeArray : maybeArrays) {
            if (maybeArray.getClass().isArray()) {
                Collections.addAll(results, flatten((Object[]) maybeArray));
            } else if (maybeArray instanceof Collection) {
                Collection<?> coll = (Collection<?>) maybeArray;
                Collections.addAll(results, flatten(coll.toArray()));
            } else {
                results.add((TreeOrMeta) maybeArray);
            }
        }
        return results.toArray(new TreeOrMeta[results.size()]);
    }
}
