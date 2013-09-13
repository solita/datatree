// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.Scanner;

public class IOUtil {

    public static String toString(InputStream in, Charset charset) {
        return new Scanner(in, charset.name()).useDelimiter("\\A").next();
    }

    public static String toString(Path path, Charset charset) throws IOException {
        return new String(Files.readAllBytes(path), charset);
    }
}
