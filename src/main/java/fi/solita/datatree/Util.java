// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree;

import java.util.*;

class Util {

    public static Object[] flatten(Object[] xs) {
        List<Object> results = new ArrayList<>();
        for (Object x : xs) {
            if (x.getClass().isArray()) {
                Collections.addAll(results, flatten((Object[]) x));
            } else if (x instanceof Collection) {
                Collection<?> coll = (Collection<?>) x;
                Collections.addAll(results, flatten(coll.toArray()));
            } else {
                results.add(x);
            }
        }
        return results.toArray(new Object[results.size()]);
    }

    public static String filterOneString(Object[] xs) {
        String result = null;
        for (Object x : xs) {
            if (x instanceof String) {
                if (result == null) {
                    result = (String) x;
                } else {
                    throw new IllegalArgumentException("Cannot have more than one text string; " +
                            "tried to add \"" + x + "\" when already had \"" + result + "\"");
                }
            }
        }
        if (result == null) {
            result = "";
        }
        return result;
    }

    public static List<Meta> filterMeta(Object[] xs) {
        List<Meta> result = new ArrayList<>(xs.length);
        for (Object x : xs) {
            if (x instanceof Meta) {
                result.add((Meta) x);
            }
        }
        return Arrays.asList(result.toArray(new Meta[result.size()]));
    }

    public static List<Tree> filterTree(Object[] xs) {
        List<Tree> result = new ArrayList<>(xs.length);
        for (Object x : xs) {
            if (x instanceof Tree) {
                result.add((Tree) x);
            }
        }
        return Arrays.asList(result.toArray(new Tree[result.size()]));
    }
}
