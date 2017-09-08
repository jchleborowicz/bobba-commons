package org.bobba.tools.tests.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

public final class BobbaTestUtils {

    private static final ObjectWriter JACKSON_OBJECT_WRITER = new ObjectMapper().writerWithDefaultPrettyPrinter();

    private BobbaTestUtils() {
    }

    public static File createDirectoryIfDoesntExist(File file) {
        if (file.exists()) {
            if (!file.isDirectory()) {
                throw new RuntimeException("Expected directory for " + file);
            }
        } else {
            if (!file.mkdir()) {
                throw new RuntimeException("Couldn't create directory " + file);
            }
        }
        return file;
    }

    public static void initializeWithCustomClassLoader(String... specialClassNames) throws ClassNotFoundException {
        final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        final URLClassLoader classLoader = new URLClassLoader(new URL[]{}, currentClassLoader) {
            @Override
            public InputStream getResourceAsStream(String name) {
                if (name.charAt(0) == '/') {
                    name = name.substring(1, name.length());
                }
                return super.getResourceAsStream(name);
            }
        };
        Thread.currentThread().setContextClassLoader(classLoader);
        try {
            for (String specialClassName : specialClassNames) {
                Class.forName(specialClassName);
            }
        } finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }

    public static String toPrettyJson(Object object) {
        try {
            return JACKSON_OBJECT_WRITER.writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void printPrettyJson(Object response) {
        System.out.println(toPrettyJson(response));
    }

}
