// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree;

import java.util.Objects;

public class Meta extends Node {

    private final String name;
    private final String value;

    public Meta(String name, String value) {
        Objects.requireNonNull(name, "name must be non-null");
        Objects.requireNonNull(value, "value must be non-null");
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return "{" + name + " \"" + value + "\"}";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Meta)) {
            return false;
        }
        Meta that = (Meta) obj;
        return this.name.equals(that.name) &&
                this.value.equals(that.value);
    }
}
