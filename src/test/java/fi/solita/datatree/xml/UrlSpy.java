// Copyright © 2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.datatree.xml;

import net.sf.cglib.proxy.*;
import sun.misc.Launcher;

import java.lang.reflect.*;
import java.net.*;
import java.util.*;

public class UrlSpy {

    public static final List<URL> openedConnections = Collections.synchronizedList(new ArrayList<URL>());

    static {
        final URLStreamHandlerFactory defaultFactory = getDefaultURLStreamHandlerFactory();

        URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {
            @Override
            public URLStreamHandler createURLStreamHandler(String protocol) {
                URLStreamHandler realHandler = defaultFactory.createURLStreamHandler(protocol);
                return (URLStreamHandler) Enhancer.create(realHandler.getClass(), new MethodInterceptor() {
                    @Override
                    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                        if (method.getName().equals("openConnection")) {
                            URL url = (URL) args[0];
                            openedConnections.add(url);
                        }
                        return proxy.invokeSuper(obj, args);
                    }
                });
            }
        });
    }

    private static URLStreamHandlerFactory getDefaultURLStreamHandlerFactory() {
        try {
            Field factory = Launcher.class.getDeclaredField("factory");
            factory.setAccessible(true);
            return (URLStreamHandlerFactory) factory.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
