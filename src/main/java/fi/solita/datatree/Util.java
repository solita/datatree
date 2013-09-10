// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree;

import java.util.*;

class Util {

    public static Object[] flatten(Object[] xs) {
        List<Object> results = new ArrayList<>();
        flatten(results, xs);
        return results.toArray(new Object[results.size()]);
    }

    private static void flatten(List<Object> results, Object item) {
        if (item == null) {
            // ignore; we consider null to be equivalent to an empty list, the same way as Clojure's nil
        } else if (item.getClass().isArray()) {
            Object[] xs = (Object[]) item;
            flatten(results, Arrays.asList(xs));
        } else if (item instanceof Iterable) {
            Iterable<?> xs = (Iterable<?>) item;
            for (Object x : xs) {
                flatten(results, x);
            }
        } else {
            checkIfAllowedType(item);
            results.add(item);
        }
    }

    private static void checkIfAllowedType(Object item) {
        Class<?> c = item.getClass();
        if (c == String.class || c == Tree.class || c == Meta.class) {
            return;
        }
        throw new IllegalArgumentException("Cannot contain " + c.getName() + " instances; tried to add " + item);
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
