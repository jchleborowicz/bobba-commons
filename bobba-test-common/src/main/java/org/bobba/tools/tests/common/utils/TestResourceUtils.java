package org.bobba.tools.tests.common.utils;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

import static org.apache.commons.lang3.Validate.notNull;

public final class TestResourceUtils {

    private TestResourceUtils() {
    }

    public static String readStream(InputStream input) {
        try {
            return IOUtils.toString(input);
        } catch (IOException e) {
            throw new RuntimeException("Error opening " + input);
        }
    }

    public static String loadFromFile(String fileName, Object contextObject) {
        final String effectiveFileName = removeLeadingSlash(fileName);
        final InputStream input = TestResourceUtils.class.getClassLoader().getResourceAsStream(effectiveFileName);
        if (input == null) {
            throw new RuntimeException("File " + effectiveFileName + " does not exist");
        }

        try {
            return loadFromStream(input, contextObject);
        } catch (RuntimeException e) {
            throw new RuntimeException("Exception when replacing placeholders in file " + effectiveFileName, e);
        }
    }

    private static String removeLeadingSlash(String fileName) {
        return fileName.startsWith("/") ? fileName.substring(1) : fileName;
    }

    public static String loadFromStream(InputStream inputStream, Object contextObject) {
        notNull(inputStream, "Input stream is null");
        final String text = readStream(inputStream);
        return ExpressionReplace.replaceExpressions(text, contextObject);
    }

    public static String loadFromFile(String fileName) {
        return loadFromFile(fileName, null);
    }

    public static <V> String loadFromFile(String fileName, String key, V value) {
        final ImmutableMap<String, V> contextObject = ImmutableMap.of(key, value);
        return loadFromFile(fileName, contextObject);
    }

    public static <V> String loadFromFile(String fileName, String key1, V value1, String key2, V value2) {
        final ImmutableMap<String, V> contextObject = ImmutableMap.of(key1, value1, key2, value2);
        return loadFromFile(fileName, contextObject);
    }

    public static <V> String loadFromFile(String fileName, String key1, V value1, String key2, V value2,
                                          String key3, V value3) {
        final ImmutableMap<String, V> contextObject = ImmutableMap.of(key1, value1, key2, value2, key3, value3);
        return loadFromFile(fileName, contextObject);
    }

    public static <V> String loadFromFile(String fileName, String key1, V value1, String key2, V value2,
                                          String key3, V value3, String key4, V value4) {
        final ImmutableMap<String, V> contextObject =
                ImmutableMap.of(key1, value1, key2, value2, key3, value3, key4, value4);
        return loadFromFile(fileName, contextObject);
    }

    public static <V> String loadFromStream(InputStream inputStream, String key, V value) {
        final ImmutableMap<String, V> contextObject = ImmutableMap.of(key, value);
        return loadFromStream(inputStream, contextObject);
    }

    public static <V> String loadFromStream(InputStream inputStream, String key1, V value1, String key2, V value2) {
        final ImmutableMap<String, V> contextObject = ImmutableMap.of(key1, value1, key2, value2);
        return loadFromStream(inputStream, contextObject);
    }

    public static <V> String loadFromStream(InputStream inputStream, String key1, V value1, String key2, V value2,
                                          String key3, V value3) {
        final ImmutableMap<String, V> contextObject = ImmutableMap.of(key1, value1, key2, value2, key3, value3);
        return loadFromStream(inputStream, contextObject);
    }

    public static <V> String loadFromStream(InputStream inputStream, String key1, V value1, String key2, V value2,
                                          String key3, V value3, String key4, V value4) {
        final ImmutableMap<String, V> contextObject =
                ImmutableMap.of(key1, value1, key2, value2, key3, value3, key4, value4);
        return loadFromStream(inputStream, contextObject);
    }

}
