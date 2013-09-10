// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree.xml;

public class ValidationException extends RuntimeException {

    public ValidationException() {
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
