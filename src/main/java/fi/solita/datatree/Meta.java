// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree;

import java.util.Objects;

public final class Meta {

    private final String name;
    private final String value;

    public Meta(String name, String value) {
        Objects.requireNonNull(name, "name must be non-null");
        Objects.requireNonNull(value, "value must be non-null");
        this.name = name;
        this.value = value;
    }

    public String name() {
        return name;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Meta)) {
            return false;
        }
        Meta that = (Meta) obj;
        return this.name.equals(that.name) &&
                this.value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        sb.append(name);
        if (!value.isEmpty()) {
            sb.append(' ').append('"').append(value).append('"');
        }
        sb.append('}');
        return sb.toString();
    }
}
